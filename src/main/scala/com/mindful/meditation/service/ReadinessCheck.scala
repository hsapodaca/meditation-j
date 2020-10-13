package com.mindful.meditation.service

import cats.Applicative
import cats.implicits._
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait ReadinessCheck[F[_]] {
  def check(): F[ReadinessCheck.ReadinessCheckResponse]
}

object ReadinessCheck {
  implicit def apply[F[_]](implicit ev: ReadinessCheck[F]): ReadinessCheck[F] = ev

  def impl[F[_]: Applicative]: ReadinessCheck[F] =
    new ReadinessCheck[F] {
      def check(): F[ReadinessCheck.ReadinessCheckResponse] =
        ReadinessCheckResponse(ConfigFactory.load()).pure[F]
    }

  final case class Name(name: String) extends AnyVal

  final case class ReadinessCheckResponse(conf: Config) extends AnyVal {
    def defaultMeditation: String = conf.getString("meditation.script.default.name")
    def defaultTherapist: String = conf.getString("therapist.script.default.name")
  }

  object ReadinessCheckResponse {
    implicit val greetingEncoder: Encoder[ReadinessCheckResponse] = new Encoder[ReadinessCheckResponse] {
      final def apply(a: ReadinessCheckResponse): Json =
        Json.obj(
          ("status", Json.fromString("UP")),
          ("defaultMeditation", Json.fromString(a.defaultMeditation)),
          ("defaultTherapist", Json.fromString(a.defaultTherapist))
        )
    }

    implicit def greetingEntityEncoder[F[_]: Applicative]
        : EntityEncoder[F, ReadinessCheckResponse] =
      jsonEncoderOf[F, ReadinessCheckResponse]
  }
}
