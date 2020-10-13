lazy val Http4sVersion = "0.21.5"
lazy val CirceVersion = "0.13.0"
lazy val Specs2Version = "4.10.0"
lazy val LogbackVersion = "1.2.3"
lazy val DoobieVersion = "0.9.0"
lazy val FlywayVersion = "7.0.2"
lazy val PureConfigVersion = "0.14.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.mindful",
    name := "meditation",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.2",
    coverageEnabled := true,
    coverageMinimum := 80,
    coverageFailOnMinimum := true,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "org.specs2" %% "specs2-core" % Specs2Version % "test",
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
