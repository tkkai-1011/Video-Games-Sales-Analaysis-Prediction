//add your code here
import org.apache.spark.{SparkContext, sql}
import org.apache.spark.sql.**
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD

import scala.reflect.internal.util.TableDef.Column
import scala.util.control.Breaks._




object Analysis {


    val input = scala.io.StdIn.readLine()
    // Key features
    //
    // Name, Platform, Year_of_Release, Genre, Publisher, Global_Sales, Critic_Score, Critic_Count


    val spark = SparkSession
      .builder()
      .appName("Spark")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    spark.sparkContext().setLogLevel("ERROR");

    val df = spark.read.option("header", "true").option("nullValue", "?").option("inferSchema", "true").csv("Datasource/Video_Game_Sales_as_at_22_Dec_2016_2")

    df.show()



    df.columns = df.columns.str.lower()


    df.select("Global_Sales").show
    df.registerTempTable("table")
    sqlContext.sql("select sum(Global_Sales) from table").show



  //Method sale by gene
    def Gene_Sale (df:sql.DataFrame, column: String, value: Int) : sql.DataFrame = {
        val Gene_sale = df
    }


    // Method sale by platform

    def

    //Method sale by publisher
    def

    //Method sale by
}





