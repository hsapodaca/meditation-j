package com.mindful.meditation.web

import cats.effect.Sync
import cats.implicits._
import com.mindful.meditation.{ReadinessCheck, Jokes}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
object MeditationRoutes {

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
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
