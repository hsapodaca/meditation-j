package com.mindful.meditation

import cats.effect.{IO, Resource}
import pureconfig._
import pureconfig.generic.auto._

package object config {

  case class Entity(name: String, summary: String, script: String)

  val defaultTherapist =
    ConfigSource.default.at("therapist.default").loadOrThrow[Entity]
  val defaultMeditation =
    ConfigSource.default.at("meditation.default").loadOrThrow[Entity]
}
