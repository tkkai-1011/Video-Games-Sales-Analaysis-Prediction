//add your code here
import org.apache.spark.sql.**
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD
import spark.implicits._



object Analysis {

  val input = scala.io.StdIn.readLine()

  val spark = SparkSession
    .builder()
    .appName("Spark SQL analysis")
    .config("spark.some.config.option", "some-value")
    .getOrCreate()

  spark.sparkContext().setLogLevel("ERROR");

  val df = spark.read.option("header", "true") .option("nullValue", "?") .option("inferSchema", "true").csv("Datasource/Video_Game_Sales_as_at_22_Dec_2016")

  df.show()

  // Key features
  //
  // Name, Platform, Year_of_Release, Genre, Publisher, Global_Sales, Critic_Score, Critic_Count

  df.columns = df.columns.str.lower()




  df.select("Global_Sales").show
  df.registerTempTable("table")
  sqlContext.sql("select sum(Global_Sales) from table").show


}





