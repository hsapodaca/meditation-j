lazy val Http4sVersion = "0.21.7"
lazy val CirceVersion = "0.13.0"
lazy val ScalaTestVersion = "3.2.0"
lazy val ScalaCheckVersion = "1.14.3"
lazy val ScalaTestPlusVersion = "3.2.2.0"
lazy val LogbackVersion = "1.2.3"
lazy val DoobieVersion = "0.9.2"
lazy val FlywayVersion = "7.0.2"
lazy val PureConfigVersion = "0.14.0"
lazy val CatsVersion = "2.2.0"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.hsapodaca",
    name := "meditation",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.3",
    coverageEnabled := true,
    coverageMinimum := 80,
    coverageFailOnMinimum := true,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % CatsVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % "test",
      "org.scalacheck" %% "scalacheck" % ScalaCheckVersion % Test,
      "org.scalatestplus" %% "scalacheck-1-14" % ScalaTestPlusVersion % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      // Postgres database
      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari" % DoobieVersion,
      "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,
      "org.flywaydb" % "flyway-core" % FlywayVersion
    )
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings"
)
