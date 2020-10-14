package io.github.hsapodaca.service

import cats.data.NonEmptyList
import cats.effect.IO
import io.github.hsapodaca.alg.Meditation
import io.github.hsapodaca.repository.MeditationRepository
import org.http4s._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Router
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class MeditationEndpointsSpec
  extends AnyFunSuite
    with Matchers
    with Http4sDsl[IO]
    with Http4sClientDsl[IO] {

}