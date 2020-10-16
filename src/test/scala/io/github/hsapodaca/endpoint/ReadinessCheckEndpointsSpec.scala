package io.github.hsapodaca

import cats.effect.IO
import io.circe.generic.auto._
import io.github.hsapodaca.alg.StatusInfo
import io.github.hsapodaca.endpoint.repos.therapists
import io.github.hsapodaca.web.ReadinessCheckEndpoints
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.{Method, Request, Response, Status, Uri}
import org.http4s.implicits.{
  http4sKleisliResponseSyntaxOptionT,
  http4sLiteralsSyntax
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReadinessCheckSpec extends AnyFlatSpec with Matchers {

  "GET /v1/entities" should "respond with 200" in {
    assert(resp.status === Status.Ok)
  }

  it should "return status details" in {
    assert(
      resp.as[StatusInfo].unsafeRunSync() === StatusInfo(
        defaultMeditation = "Leaves on a Stream Meditation",
        defaultTherapist = "J"
      )
    )
  }

  private[this] val resp: Response[IO] = get(uri"/status")

  private[this] def get(uri: Uri): Response[IO] = {
    val req = Request[IO](Method.GET, uri)
    ReadinessCheckEndpoints
      .endpoints(therapists)
      .orNotFound(req)
      .unsafeRunSync()
  }
}
