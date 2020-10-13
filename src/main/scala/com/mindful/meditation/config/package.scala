package com.mindful.meditation

import cats.effect.{IO, Resource}
import pureconfig._
import pureconfig.generic.auto._

package object config {

  case class Entity(name: String, summary: String, script: String)

  case class DatabaseConnection(driver: String, url: String, user: String, password: String)

  val defaultTherapist =
    ConfigSource.default.at("therapist.default").loadOrThrow[Entity]
  val defaultMeditation =
    ConfigSource.default.at("meditation.default").loadOrThrow[Entity]

  val databaseConnection = ConfigSource.default.at("database").loadOrThrow[DatabaseConnection]
}
