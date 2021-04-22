package controllers

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.Estimator
import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer, VectorAssembler}
import org.apache.spark.ml.regression.LinearRegression

import javax.inject.Inject
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.sql.functions._
import play.api.mvc._
import scala.collection._

import play.api.i18n.I18nSupport

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

case class BasicForm(Fir_feature: String, Sec_feature: String, Thr_feature: String)
case class Simple(res: String)
// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object BasicForm {
  import play.api.data.Forms._
  import play.api.data.Form
  val form: Form[BasicForm] = Form(
    mapping(
      "Fir_feature" -> nonEmptyText,
      "Sec_feature" -> text,
      "Thr_feature" -> text
    )(BasicForm.apply)(BasicForm.unapply)
  )
}
object Simple {
  import play.api.data.Forms._
  import play.api.data.Form
  val form: Form[Simple] = Form(
    mapping(
      "res" -> text
    )(Simple.apply)(Simple.unapply)
  )
}

//case class GameInfo (Name: String, Platform: String, Year_of_Release: String, Genre: String, Publisher: String, NA_Sales: Double, EU_Sales: Double, JP_Sales: Double, Other_Sales: Double, Global_Sales: Double, Critic_Score: Integer, Critic_Count: Integer, User_Score: String, User_Count: Integer, Developer: String, Rating: String)
//class HomeController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {
//@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
//    var result = new Result("s")
//  val result = scala.collection.mutable.ListBuffer.empty[String]
  def isEmpty(x: String) = x == null || x.isEmpty


  def process(feature1: String, feature2: String, feature3: String): String ={
    val spark = SparkSession
      .builder()
      .appName("Spark Hive Example")
      .config("spark.master", "local")
      .getOrCreate()
    val data = spark.read.format("csv")
      .option("sep", ",")
      .option("inferSchema", "true")
      .option("header", "true")
      .load("C:\\Users\\qiank\\Final-Scala\\Video-Games-Sales-Analaysis-Prediction\\src\\main\\scala\\Video_Games_Sales_as_at_22_Dec_2016.csv")

    val pro = scala.collection.mutable.ListBuffer.empty[String]
    val temp = scala.collection.mutable.ListBuffer.empty[String]
    pro.append("The Best result is here: ")
    pro.append("Name,Platform,Year_of_Release,Genre,Publisher,NA_Sales,EU_Sales,JP_Sales,Other_Sales,Global_Sales,Critic_Score,Critic_Count,User_Score,User_Count,Developer,Rating")
    temp.append("Top 10 Result is here: ")
    temp.append("Name,Platform,Year_of_Release,Genre,Publisher,NA_Sales,EU_Sales,JP_Sales,Other_Sales,Global_Sales,Critic_Score,Critic_Count,User_Score,User_Count,Developer,Rating")
    if (isEmpty(feature3) && !isEmpty(feature2)){
      val p = data.orderBy(desc(feature1),desc(feature2)).take(10).toList
      pro.append(p.head.toString())
      pro.append("\n")
      pro.append("\n")
      p.foreach( x => temp.append(x.toString()))
    }
    if (!isEmpty(feature2) && !isEmpty(feature3)){
      val p = data.orderBy(desc(feature1),desc(feature2),desc(feature3)).take(10).toList
      pro.append(p.head.toString())
      pro.append("\n")
      pro.append("\n")
      p.foreach( x => temp.append(x.toString()))
    }
    if (isEmpty(feature2) && isEmpty(feature3)){
      val p = data.orderBy(desc(feature1)).take(10).toList
      pro.append(p.head.toString())
      pro.append("\n")
      pro.append("\n")
      p.foreach( x => temp.append(x.toString()))
    }
    val temp1 = List.concat(pro,temp)
    val res = temp1.mkString("\n")
//      spark.stop()
      res
  }

  def predPost() = Action { implicit request =>
    val spark = SparkSession
      .builder()
      .appName("Spark Hive Example")
      .config("spark.master", "local")
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
    val pro = scala.collection.mutable.ListBuffer.empty[String]
    // Print the coefficients and intercept for linear regression
    pro.append( s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

    // Summarize the model over the training set and print out some metrics
    val trainingSummary = lrModel.summary
    pro.append( s"numIterations: ${trainingSummary.totalIterations}")
    pro.append(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
//    trainingSummary.residuals.show()
    pro.append(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    pro.append(s"r2: ${trainingSummary.r2}")

    val res = pro.mkString("\n")
    spark.stop()
    Ok(res)
  }

  def analysisPost() = Action { implicit request: Request[AnyContent] =>
    val formData: BasicForm = BasicForm.form.bindFromRequest.get // Careful: BasicForm.form.bindFromRequest returns an Option
    val res = process(formData.Fir_feature, formData.Sec_feature, formData.Thr_feature)
    Ok(res) // just returning the data because it's an example :)
  }

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
  def analysis() =  Action { implicit request: Request[AnyContent] =>
    Ok(views.html.analysis(BasicForm.form,Simple.form))
  }

  def pred() =  Action { implicit request: Request[AnyContent] =>
    Ok(views.html.prediction())
  }

}

