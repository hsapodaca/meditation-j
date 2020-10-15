package io.github.hsapodaca

import pureconfig._
import pureconfig.generic.auto._

package object config {

  case class ConfiguredEntity(name: String, summary: String, script: String)


  case class DatabaseConnection(
      driver: String,
      url: String,
      user: String,
      password: String,
      poolSize: Int
  )

  case class Server(host: String, port: Int)

  lazy val defaultTherapist =
    ConfigSource.default.at("therapist.default").loadOrThrow[ConfiguredEntity]
  lazy val defaultMeditation =
    ConfigSource.default.at("meditation.default").loadOrThrow[ConfiguredEntity]
  lazy val entitySearchLimitDefault =
    ConfigSource.default.at("entity.search.limit.default").loadOrThrow[Int]
  lazy val databaseConnection =
    ConfigSource.default.at("database").loadOrThrow[DatabaseConnection]

  lazy val server = ConfigSource.default.at("server").loadOrThrow[Server]
}
