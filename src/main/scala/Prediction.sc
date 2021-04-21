import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.SparkSession



   {
        val spark: SparkSession = SparkSession
          .builder()
          .appName("Video Games Sales")
          .master("local[*]")
          .getOrCreate()



        //Load training data
        val data = spark.read.format("csv")
          .option("header", "true")
          .option("inferSchema", "true")
          .load("/Users/anhdao/Desktop/CSYE7200/VideoGamesSales/Video-Games-Sales-Analaysis-Prediction/src/main/scala/Video_Games_Sales_as_at_22_Dec_2016 2.csv")

        //Create A model
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
        println(s"RMS: ${trainingSummary.rootMeanSquaredError}")
        //Get R2 measure
        println(s"r2: ${trainingSummary.r2}")


    }

