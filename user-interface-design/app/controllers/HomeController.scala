package controllers

import javax.inject.Inject
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.sql.functions._
import scala.collection._

//import play.api.i18n.Messages._


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

case class BasicForm(name: String)
case class Result(res: String)
// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object BasicForm {
  import play.api.data.Forms._
  import play.api.data.Form
  val form: Form[BasicForm] = Form(
    mapping(
      "name" -> text
    )(BasicForm.apply)(BasicForm.unapply)
  )
}

//case class GameInfo (Name: String, Platform: String, Year_of_Release: String, Genre: String, Publisher: String, NA_Sales: Double, EU_Sales: Double, JP_Sales: Double, Other_Sales: Double, Global_Sales: Double, Critic_Score: Integer, Critic_Count: Integer, User_Score: String, User_Count: Integer, Developer: String, Rating: String)
//class HomeController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {
//@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def process(feature: String): String ={
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

    val res =data.orderBy(desc(feature)).first().toString()

    spark.stop()
    res
  }

//  def show() = Action {implicit request =>
//    Ok(views.html.show)
//  }

  def analysisPost() = Action { implicit request =>
    val formData: BasicForm = BasicForm.form.bindFromRequest.get // Careful: BasicForm.form.bindFromRequest returns an Option
    val res = process(formData.name)
    Ok(res) // just returning the data because it's an example :)
  }

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
  def analysis() =  Action { implicit request: Request[AnyContent] =>
    Ok(views.html.analysis(BasicForm.form))
  }

  def pred() =  Action { implicit request: Request[AnyContent] =>
    Ok(views.html.prediction())
  }

}

