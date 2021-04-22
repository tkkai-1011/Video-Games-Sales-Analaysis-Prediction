import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer, VectorAssembler}
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.SparkSession

val spark: SparkSession = SparkSession
  .builder()
  .appName("Video Games Sales")
  .master("local[*]")
  .getOrCreate()


// Load training data
val VideoGamesSales = spark.read.format("csv")
  .option("sep", ",")
  .option("header", "true")
  .option("inferSchema", "true")
  .load("C:\\Users\\qiank\\Final-Scala\\Video-Games-Sales-Analaysis-Prediction\\src\\main\\scala\\Video_Games_Sales_as_at_22_Dec_2016.csv")

//Transform string type columns to string indexer
//  val NameIndexer = new StringIndexer().setInputCol("Name").setOutputCol("NameIndex").fit(VideoGamesSales).transform(VideoGamesSales)
//  val PlatformIndexer = new StringIndexer().setInputCol("Platform").setOutputCol("PlatformIndex").fit(VideoGamesSales).transform(VideoGamesSales)
//  val Year_of_ReleaseIndexer = new StringIndexer().setInputCol("Year_of_Release").setOutputCol("Year_of_Release_Index").fit(VideoGamesSales).transform(VideoGamesSales)
//  val GenreIndexer = new StringIndexer().setInputCol("Genre").setOutputCol("GenreIndex").fit(VideoGamesSales).transform(VideoGamesSales)
//  val PublisherIndexer = new StringIndexer().setInputCol("Publisher").setOutputCol("PublisherIndex").fit(VideoGamesSales).transform(VideoGamesSales)
//  val User_ScoreIndexer = new StringIndexer().setInputCol("User_Score").setOutputCol("User_ScoreIndex").fit(VideoGamesSales).transform(VideoGamesSales)
//  val DeveloperIndexer = new StringIndexer().setInputCol("Developer").setOutputCol("DeveloperIndex").fit(VideoGamesSales).transform(VideoGamesSales)
//  val RatingIndexer = new StringIndexer().setInputCol("Rating").setOutputCol("RatingIndex").fit(VideoGamesSales).transform(VideoGamesSales)

//  val NameEncoder = new OneHotEncoder().setInputCol("NameIndex").setOutputCol("NameVector")
//  val PlatformEncoder = new OneHotEncoder().setInputCol("PlatformIndex").setOutputCol("PlatformVec")
//  val Year_of_ReleaseEncoder = new OneHotEncoder().setInputCol("Year_of_Release_Index").setOutputCol("Year_of_ReleaseVec")
//  val GenreEncoder = new OneHotEncoder().setInputCol("GenreIndex").setOutputCol("GenreVec")
//  val PublisherEncoder = new OneHotEncoder().setInputCol("PublisherIndex").setOutputCol("PublisherVec")
//  val User_ScoreEncoder = new OneHotEncoder().setInputCol("User_ScoreIndex").setOutputCol("User_ScoreVec")
//  val DeveloperEncoder = new OneHotEncoder().setInputCol("DeveloperIndex").setOutputCol("DeveloperVec")
//  val RatingEncoder = new OneHotEncoder().setInputCol("RatingIndex").setOutputCol("RatingVec")


// Drop NA values
val NoNullVideoGamesSales = VideoGamesSales.na.drop()

// Filter for our required features columns
val features = NoNullVideoGamesSales.columns.filterNot(_.contains("Sales")).filterNot(_.contains("r_Score"))
  .filterNot(_.contains("Developer")).filterNot(_.contains("Rating"))

// Transform string type columns to numerical
val encodedFeatures = features.flatMap { name =>
  val indexer = new StringIndexer()
    .setInputCol(name)
    .setOutputCol(name + "_Index")

  val oneHotEncoder = new OneHotEncoder()
    .setInputCols(Array(name + "_Index"))
    .setOutputCols(Array(name + "_vec"))
    .setDropLast(false)

  Array(indexer, oneHotEncoder)
}

// Fit dataframe transformations into a pipeline
val pipeline = new Pipeline().setStages(encodedFeatures)

val indexer_model = pipeline.fit(NoNullVideoGamesSales)

val df_transformed = indexer_model.transform(NoNullVideoGamesSales)

val vecFeatures = df_transformed.columns.filter(_.contains("vec"))


//VectorAssembler
val assembler = new VectorAssembler().setInputCols(vecFeatures)
  .setOutputCol("features")

val pipelinelineVectorAssembler = new Pipeline().setStages(Array(assembler))

val result_df = pipelinelineVectorAssembler.fit(df_transformed).transform(df_transformed)


//Splits data
val Array(trainingData, testData) = result_df.randomSplit(Array(0.8, 0.2))


//Create Linear Regression Model
val lr = new LinearRegression()
  .setMaxIter(10)
  .setRegParam(0.1)
  .setElasticNetParam(0.8)
  .setLabelCol("Global_Sales")
  .setFeaturesCol("features")


// Fit the model
val lrModel = lr.fit(trainingData)

// Make predictions
val predictions = lrModel.transform(testData)


// Select rows to display
predictions.select("prediction","Global_Sales","features").show(5)


// Select (prediction, true label) and compute test error.
import org.apache.spark.ml.evaluation.RegressionEvaluator

val evaluator = new RegressionEvaluator(). setLabelCol("Global_Sales").setPredictionCol("prediction").setMetricName("rmse")
val rmse = evaluator.evaluate(predictions)
println(s"Root Mean Squared Error (RMSE) on test data = $rmse")


// Print the coefficients and intercept for linear regression
println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

// Summarize the model over the training set and print out some metrics
val trainingSummary = lrModel.summary
println(s"numIterations: ${trainingSummary.totalIterations}")
println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
trainingSummary.residuals.show()
println(s"P Values: ${trainingSummary.pValues.mkString(",")}")
println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
println(s"r2: ${trainingSummary.r2}")

spark.stop()