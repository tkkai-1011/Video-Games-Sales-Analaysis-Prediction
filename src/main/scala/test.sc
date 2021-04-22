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

// Drop NA values
val NoNullVideoGamesSales = VideoGamesSales.na.drop()

val features = NoNullVideoGamesSales.columns.filterNot(_.contains("Sales")).filterNot(_.contains("r_Score"))
  .filterNot(_.contains("Developer")).filterNot(_.contains("Rating"))


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
  .setRegParam(0.3)
  .setElasticNetParam(0.8)
  .setLabelCol("Global_Sales")
  .setFeaturesCol("features")


// Fit the model
val lrModel = lr.fit(trainingData)


// Print the coefficients and intercept for linear regression
println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

// Summarize the model over the training set and print out some metrics
val trainingSummary = lrModel.summary
val x = s"numIterations: ${trainingSummary.totalIterations}"
println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
trainingSummary.residuals.show()
println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
println(s"r2: ${trainingSummary.r2}")

spark.stop()
