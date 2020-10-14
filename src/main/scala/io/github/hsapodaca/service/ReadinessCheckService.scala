package io.github.hsapodaca.service

import cats.Applicative
import cats.implicits._
import io.github.hsapodaca.config
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait ReadinessCheckService[F[_]] {
  def check(): F[ReadinessCheckService.ReadinessCheckResponse]
}

object ReadinessCheckService {
  implicit def apply[F[_]](implicit ev: ReadinessCheckService[F]): ReadinessCheckService[F] = ev

  def impl[F[_]: Applicative]: ReadinessCheckService[F] = () => ReadinessCheckResponse().pure[F]

  final case class Name(name: String) extends AnyVal

  final case class ReadinessCheckResponse() {
    def defaultMeditation: String = config.defaultMeditation.name
    def defaultTherapist: String = config.defaultTherapist.name
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
