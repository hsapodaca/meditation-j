package com.mindful.meditation

import cats.effect.IO
import com.mindful.meditation.service.ReadinessCheck
import com.mindful.meditation.web.MeditationRoutes
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

class ReadinessCheckSpec extends org.specs2.mutable.Specification {

  "ReadinessCheck" >> {
    "return 200" >> {
      uriReturns200()
    }
    "return status details" >> {
      uriReturnsValidStatus()
    }
  }

  private[this] val retStatus: Response[IO] = {
    val getHW = Request[IO](Method.GET, uri"/status")
    val readinessCheck = ReadinessCheck.impl[IO]
    MeditationRoutes
      .readinessCheckRoutes(readinessCheck)
      .orNotFound(getHW)
      .unsafeRunSync()
  }

  private[this] def uriReturns200(): MatchResult[Status] =
    retStatus.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsValidStatus(): MatchResult[String] =
    retStatus.as[String].unsafeRunSync() must beEqualTo(
      "{\"status\":\"UP\",\"defaultMeditation\":\"Leaves on a Stream Meditation\",\"defaultTherapist\":\"J\"}"
    )
}
