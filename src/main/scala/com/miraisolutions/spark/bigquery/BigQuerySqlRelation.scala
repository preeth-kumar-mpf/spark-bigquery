/*
 * Copyright (c) 2018 Mirai Solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.miraisolutions.spark.bigquery

import com.miraisolutions.spark.bigquery.client.BigQueryClient
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SQLContext}

/**
  * Relation for a Google BigQuery standard SQL query
  *
  * @param sqlContext Spark SQL context
  * @param sqlQuery BigQuery standard SQL query in SQL-2011 dialect
  */
private final class BigQuerySqlRelation(val sqlContext: SQLContext, val client: BigQueryClient, val sqlQuery: String)
  extends BaseRelation with TableScan {

  private lazy val table = client.executeQuery(sqlQuery, sqlContext.sparkContext.defaultParallelism)

  override def schema: StructType = client.getSchema(table.table.getTableId)

  override def buildScan(): RDD[Row] = new BigQueryRowRDD(sqlContext.sparkContext, table)
}
