import org.apache.spark
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

import org.apache.spark._
import org.apache.spark.graphx._
// To make some of the examples work we will also need RDD
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import java.io.File
// For implicit conversions like converting RDDs to DataFrames
//import spark.implicits._
val warehouseLocation = new File("spark-warehouse").getAbsolutePath
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

data.columns
data.printSchema()
data.orderBy(desc("NA_Sales")).show()

spark.stop()
//// Drop the first line in first partition because it is the header.
//val rdd = data.rdd.mapPartitionsWithIndex{(idx,iter) =>
//  if(idx == 0) iter.drop(1) else iter
//}
//
//// A function to create schema dynamically.
//def schemaCreator(header: String): StructType = {
//  StructType(header
//    .split(",")
//    .map(field => StructField(field.trim, StringType, true))
//  )
//}
////val temp = data.first().toString()
//// Create the schema for the csv that was read and store it.
//val csvSchema: StructType = schemaCreator(data.first().toString().replaceAll("[\\[\\]]",""))
//// As the input is CSV, split it at "," and trim away the whitespaces.
//val rdd_curated = rdd.map(x => x.toString().split(",").map(y => y.trim)).map(xy => Row(xy:_*))
//
//// Create the DF from the RDD.
//val df = spark.createDataFrame(rdd_curated, csvSchema)
//
////check Schema
//df.printSchema()
//
//df.orderBy("Name")

//val platform = df.orderBy("Name" )
//platform.show()











