import org.apache.spark
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

import java.io.File
// For implicit conversions like converting RDDs to DataFrames
//import spark.implicits._
val warehouseLocation = new File("spark-warehouse").getAbsolutePath

val spark = SparkSession
  .builder()
  .appName("Spark Hive Example")
//  .config("spark.sql.warehouse.dir", "Video-Games-Sales-Analaysis-Prediction")
  .config("spark.master", "local")
  .getOrCreate()

val data = spark.read.format("csv")
  .option("sep", ";")
  .option("inferSchema", "true")
  .option("header", "true")
  .load("C:\\Users\\qiank\\Final-Scala\\Video-Games-Sales-Analaysis-Prediction\\src\\main\\scala\\Video_Games_Sales_as_at_22_Dec_2016.csv")

data.show()
spark.stop()











