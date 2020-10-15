package io.github.hsapodaca.alg

import cats.effect.IO
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Entities_GET_Spec
  extends AnyFlatSpec
    with Matchers
    with Http4sDsl[IO]
    with Http4sClientDsl[IO] {

}