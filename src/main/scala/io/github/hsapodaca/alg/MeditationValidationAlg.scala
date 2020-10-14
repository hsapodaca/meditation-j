package io.github.hsapodaca.alg
import cats.data.EitherT

trait MeditationValidationAlg[F[_]] {
  def doesNotExist(
      entity: Meditation
  ): EitherT[F, MeditationAlreadyExistsError, Unit]

  def exists(
      entity: Option[Long]
  ): EitherT[F, MeditationNotFoundError.type, Unit]

}
