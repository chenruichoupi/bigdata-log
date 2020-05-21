package com.spark.test

import org.apache.spark.sql.SparkSession

object test {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.sql.SparkSession
    val spark = SparkSession.builder().master("local[2]").appName("Streaming").getOrCreate()
    import spark.implicits._
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "bigdata-pro01.kfk.com:9092")
      .option("subscribe", "weblogs")
      .load()
    val lines = df.selectExpr("CAST(value AS STRING)").as[(String)]
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.groupBy("value").count()
    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .start()
    query.awaitTermination()
  }

}