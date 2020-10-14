package io.github.hsapodaca

import pureconfig._
import pureconfig.generic.auto._

package object config {

  case class Entity(name: String, summary: String, script: String)

  case class DatabaseConnection(
      driver: String,
      url: String,
      user: String,
      password: String,
      poolSize: Int
  )

  case class Server(host: String, port: Int)

  val defaultTherapist =
    ConfigSource.default.at("therapist.default").loadOrThrow[Entity]
  val defaultMeditation =
    ConfigSource.default.at("meditation.default").loadOrThrow[Entity]

  val databaseConnection =
    ConfigSource.default.at("database").loadOrThrow[DatabaseConnection]

  val server = ConfigSource.default.at("server").loadOrThrow[Server]
}
