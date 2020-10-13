package com.mindful.meditation.web

import cats.effect.Sync
import cats.implicits._
import com.mindful.meditation.service.{Meditations, ReadinessCheck, Therapists}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
object MeditationRoutes {

  def meditationRoutes[F[_]: Sync](M: Meditations[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "meditations" =>
        for {
          meditation <- M.get
          resp <- Ok(meditation)
        } yield resp
    }
  }

  def therapistRoutes[F[_]: Sync](T: Therapists[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "therapists" =>
        for {
          therapist <- T.get
          resp <- Ok(therapist)
        } yield resp
    }
  }

  def readinessCheckRoutes[F[_]: Sync](H: ReadinessCheck[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "status" =>
        for {
          res <- H.check()
          resp <- Ok(res)
        } yield resp
    }
  }
}
