package com.mindful.meditation

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import com.mindful.meditation.service.{Meditations, ReadinessCheck, Therapists}
import com.mindful.meditation.web.MeditationRoutes
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object MeditationServer {

  def stream[F[_]: ConcurrentEffect](implicit
      T: Timer[F],
      C: ContextShift[F]
  ): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      readinessCheckAlg = ReadinessCheck.impl[F]
      meditationsAlg = Meditations.impl[F](client)
      therapistsAlg = Therapists.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract segments not checked
      // in the underlying routes.
      httpApp = (
          MeditationRoutes.readinessCheckRoutes[F](readinessCheckAlg) <+>
            MeditationRoutes.meditationRoutes[F](meditationsAlg) <+>
            MeditationRoutes.therapistRoutes[F](therapistsAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <-
        BlazeServerBuilder[F](global)
          .bindHttp(config.server.port, config.server.host)
          .withHttpApp(finalHttpApp)
          .serve
    } yield exitCode
  }.drain
}
