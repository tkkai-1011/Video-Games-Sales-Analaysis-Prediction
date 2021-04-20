import org.apache.spark
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import java.io.File
// For implicit conversions like converting RDDs to DataFrames
//import spark.implicits._

object AnalysisRedo extends App{
  def process(feature: String): String ={
    val spark = SparkSession
      .builder()
      .appName("Spark Hive Example")
      .config("spark.master", "local")
      .getOrCreate()

    val data = spark.read.format("csv")
      .option("sep", ",")
      .option("inferSchema", "true")
      .option("header", "true")
      .load("C:\\Users\\qiank\\Final-Scala\\Video-Games-Sales-Analaysis-Prediction\\src\\main\\scala\\Video_Games_Sales_as_at_22_Dec_2016.csv")

    data.printSchema()
    val res =data.orderBy(desc(feature)).first().toString()

    spark.stop()
    res

  }
}










