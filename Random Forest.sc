import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.ml.regression.RandomForestRegressor
import org.apache.spark.sql.SparkSession


object RandomForestVideoGames extends App {
  override def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .appName("Random Forest Video Games Sales")
      .master("local[*}")
      .getOrCreate()

    //Load data
    val data2 = spark.read.format("csv").option("header", "true").load("Video-Games-Sales-Analysis-Prediction/Video_Games_Sales_as_at_22_Dec_2016 2.csv")

    // Automatically identify categorical features, and index them.
    // Set maxCategories so features with > 4 distinct values are treated as continuous.
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(data2)

    // Split the data into training and test sets (30% held out for testing).
    val Array(trainingData, testData) = data2.randomSplit(Array(0.8, 0.2))


    //Create the RandomForest Model
    val rf = new RandomForestRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")

    //Chain indexer and forest in a Pipeline.
    val pipeline = new Pipeline()
      .setStages(Array(featureIndexer, rf))


    // Train the model
    val model = pipeline.fit(trainingData)

    //Make predictions.
    val predictions = model.transform(testData)

    // Select example rows to display.
    predictions.select("prediction", "label", "features").show(5)

    //Select (prediction, true label) and compute test error.
    val evaluator = new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("rmse")
    val rmse = evaluator.evaluate(predictions)
    println(s"Root Mean Squared Error (RMSE) on test data = $rmse")

  }
}



