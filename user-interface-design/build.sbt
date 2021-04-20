
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """User-Interface-Design""",
    version := "2.6.x",
    scalaVersion := "2.12.13",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
    ),
    libraryDependencies ++= Seq(
      "org.apache.spark" % "spark-core_2.12" % "3.1.1",
      "org.apache.spark" % "spark-sql_2.12" % "3.1.1",
      "org.apache.spark" % "spark-hive_2.12" % "3.1.1"
    ),
    libraryDependencies +=
      "org.apache.spark" %% "spark-mllib" % "3.1.1" % "provided",
        scalacOptions ++= Seq(
          "-feature",
          "-deprecation",
          "-Xfatal-warnings"
        )
  )