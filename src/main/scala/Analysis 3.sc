//add your code here
import org.apache.spark.sql.SparkSession
import spark.implicits._


val input = scala.io.StdIn.readLine()

val spark = SparkSession
  .builder()
  .appName("Spark SQL basic example")
  .config("spark.some.config.option", "some-value")
  .getOrCreate()

val df = spark.read.csv("Datasource/Video_Game_Sales_as_at_22_Dec_2016")

//
// Name, Platform, Year of release, genre, publisher, global sales, critic score, critic count











