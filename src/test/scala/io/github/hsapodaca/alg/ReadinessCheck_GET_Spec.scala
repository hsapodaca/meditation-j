package io.github.hsapodaca.alg

import cats.effect.IO
import io.github.hsapodaca.endpoint.ReadinessCheckEndpoints
import org.http4s._
import org.http4s.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReadinessCheck_GET_Spec extends AnyFlatSpec with Matchers {

  "GET /status" should "respond with 200" in {
    assert(resp.status === Status.Ok)
  }

  it should "return status details" in {
    assert(resp.as[String].unsafeRunSync() ===
      "{\"status\":\"UP\",\"defaultMeditation\":\"Leaves on a Stream Meditation\",\"defaultTherapist\":\"J\"}")
  }

  private[this] val resp: Response[IO] = get(uri"/status")

  private[this] def get(uri: Uri): Response[IO] = {
    val req = Request[IO](Method.GET, uri)
    val readinessCheck = ReadinessCheckService[IO]()
    ReadinessCheckEndpoints
      .endpoints(readinessCheck)
      .orNotFound(req)
      .unsafeRunSync()
  }
}
