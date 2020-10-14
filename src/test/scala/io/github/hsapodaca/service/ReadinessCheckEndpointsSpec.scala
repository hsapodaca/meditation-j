package io.github.hsapodaca.service

import cats.effect.IO
import io.github.hsapodaca.endpoint.ReadinessCheckEndpoints
import org.http4s._
import org.http4s.implicits._
import org.scalatest.flatspec.AnyFlatSpec

class ReadinessCheckEndpointsSpec extends AnyFlatSpec {

  "Readiness Check" should "respond with 200" in {
    retStatus.status === Status.Ok
  }

  it should "return status details" in {
    retStatus.as[String].unsafeRunSync() ===
      "{\"status\":\"UP\",\"defaultMeditation\":\"Leaves on a Stream Meditation\",\"defaultTherapist\":\"J\"}"
  }

  private[this] val retStatus: Response[IO] = {
    val getHW = Request[IO](Method.GET, uri"/status")
    val readinessCheck = ReadinessCheckService.impl[IO]
    ReadinessCheckEndpoints
      .endpoints(readinessCheck)
      .orNotFound(getHW)
      .unsafeRunSync()
  }
}
