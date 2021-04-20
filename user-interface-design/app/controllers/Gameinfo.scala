//package controllers
//
//object GameInfoForm {
//  import play.api.data.Forms._
//  import play.api.data.Form
//
//  /**
//   * A form processing DTO that maps to the form below.
//   *
//   * Using a class specifically for form binding reduces the chances
//   * of a parameter tampering attack and makes code clearer.
//   */
//  case class Data(Name: String, Platform: String, Year_of_Release: String, Genre: String, Publisher: String, NA_Sales: Double, EU_Sales: Double, JP_Sales: Double, Other_Sales: Double, Global_Sales: Double, Critic_Score: Integer, Critic_Count: Integer, User_Score: String, User_Count: Integer, Developer: String, Rating: String)
//
//  /**
//   * The form definition for the "create a widget" form.
//   * It specifies the form fields and their types,
//   * as well as how to convert from a Data to form data and vice versa.
//   */
//  val form = Form(
//    mapping(
//      "Name" -> nonEmptyText,
//      "Platform" -> nonEmptyText,
//      "Year_of_Release" -> nonEmptyText,
//      "Genre" -> nonEmptyText,
//      "Publisher" -> nonEmptyText,
//      "NA_Sales" -> number(min = 0)
//    )(Data.apply)(Data.unapply)
//  )
//}