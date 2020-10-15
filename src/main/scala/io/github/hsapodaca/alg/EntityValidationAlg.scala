package io.github.hsapodaca.alg
import cats.data.EitherT

trait EntityValidationAlg[F[_]] {
  def doesNotExist(
      entity: Entity
  ): EitherT[F, EntityAlreadyExistsError, Unit]

  def exists(
      entity: Option[Long]
  ): EitherT[F, EntityNotFoundError.type, Unit]
}
