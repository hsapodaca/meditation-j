package io.github.hsapodaca.service

import cats.effect.IO
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Meditations_GET_Spec
  extends AnyFlatSpec
    with Matchers
    with Http4sDsl[IO]
    with Http4sClientDsl[IO] {

}