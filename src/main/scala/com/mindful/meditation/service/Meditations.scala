package com.mindful.meditation.service

import cats.Applicative
import cats.effect.Sync
import cats.implicits._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import org.http4s.Method._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._
import org.http4s.{EntityDecoder, EntityEncoder}

trait Meditations[F[_]] {
  def get: F[Meditations.Meditation]
}

object Meditations {
  def apply[F[_]](implicit ev: Meditations[F]): Meditations[F] = ev

  def impl[F[_]: Sync](C: Client[F]): Meditations[F] =
    new Meditations[F] {
      val dsl = new Http4sClientDsl[F] {}

      import dsl._

      def get: F[Meditations.Meditation] = {
        ???
      }
    }

  final case class Meditation(name: String, summary: String, script: String)

  final case class MeditationError(e: Throwable) extends RuntimeException

  object Meditation {
    implicit val MeditationDecoder: Decoder[Meditation] = deriveDecoder[Meditation]

    implicit def MeditationEntityDecoder[F[_]: Sync]: EntityDecoder[F, Meditation] =
      jsonOf

    implicit val MeditationEncoder: Encoder[Meditation] = deriveEncoder[Meditation]

    implicit def MeditationEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Meditation] =
      jsonEncoderOf
  }
}
