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

trait Therapists[F[_]] {
  def get: F[Therapists.Therapist]
}

object Therapists {
  def apply[F[_]](implicit ev: Therapists[F]): Therapists[F] = ev

  def impl[F[_]: Sync](C: Client[F]): Therapists[F] =
    new Therapists[F] {
      val dsl = new Http4sClientDsl[F] {}

      import dsl._

      def get: F[Therapists.Therapist] = {
        ???
      }
    }

  final case class Therapist(name: String, summary: String, script: String)

  final case class TherapistError(e: Throwable) extends RuntimeException

  object Therapist {
    implicit val TherapistDecoder: Decoder[Therapist] = deriveDecoder[Therapist]

    implicit def TherapistEntityDecoder[F[_]: Sync]: EntityDecoder[F, Therapist] =
      jsonOf

    implicit val TherapistEncoder: Encoder[Therapist] = deriveEncoder[Therapist]

    implicit def TherapistEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Therapist] =
      jsonEncoderOf
  }
}
