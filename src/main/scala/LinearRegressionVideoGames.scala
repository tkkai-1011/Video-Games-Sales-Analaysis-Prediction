import org.apache.spark
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.regression.LinearRegression


object LinearRegressionVideoGames extends App {
    def app(): Unit = {
        val spark: SparkSession = SparkSession
          .builder()
          .appName("Video Gamesv Sales")
          .master("local[*]")
          .getOrCreate()

        //Load training data
        val data = spark.read.format("csv")
          .option("header", "true")
          .option("inferSchema", "true")
          .load("Video-Games-Sales-Analysis-Prediction/src/main/scala/Video_Games_Sales_as_at_22_Dec_2016 2.csv")

        //Create A mo
        val lr = new LinearRegression()
          .setMaxIter(10)
          .setRegParam(0.3)
          .setElasticNetParam(0.8)

        // Fit the model
        val lrModel = lr.fit(data)

        // Print the coefficients and intercept for linear regression
        println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

        // Summarize the model over the training set and print out some metrics
        val trainingSummary = lrModel.summary
        println(s"numIterations: ${trainingSummary.totalIterations}")
        println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
        trainingSummary.residuals.show()
        println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
        //Get R2 measure
        println(s"r2: ${trainingSummary.r2}")

        spark.stop()
    }
}

