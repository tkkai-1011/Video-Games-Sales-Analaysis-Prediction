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
def isEmpty(x: String) = x == null || x.isEmpty

val data = spark.read.format("csv")
  .option("sep", ",")
  .option("inferSchema", "true")
  .option("header", "true")
  .load("C:\\Users\\qiank\\Final-Scala\\Video-Games-Sales-Analaysis-Prediction\\src\\main\\scala\\Video_Games_Sales_as_at_22_Dec_2016.csv")

val feature2 = "Name"
val temp = scala.collection.mutable.ListBuffer.empty[String]
temp.append("Name,Platform,Year_of_Release,Genre,Publisher,NA_Sales,EU_Sales,JP_Sales,Other_Sales,Global_Sales,Critic_Score,Critic_Count,User_Score,User_Count,Developer,Rating")
//var temp1 =data.orderBy(desc("NA_Sales"),desc("Global_Sales"),desc("Platform")).take(10).toList
if (!isEmpty(feature2)){
  data.orderBy(desc(feature2)).take(10).toList.foreach( x => temp.append(x.toString()))
}
val res = temp.mkString("\n")
print(res)


spark.stop()












