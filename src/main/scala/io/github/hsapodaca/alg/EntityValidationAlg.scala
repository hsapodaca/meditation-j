package io.github.hsapodaca.alg
import cats.data.EitherT

trait EntityValidationAlg[F[_]] {
  def doesNotExist(
      entity: Entity
  ): EitherT[F, ItemAlreadyExistsError.type, Unit]

  def entityNameDoesNotExist(
      entity: Entity
  ): EitherT[F, EntityIsInvalidForUpdateError, Unit]

  def exists(
      id: Option[Long]
  ): EitherT[F, EntityNotFoundError.type, Unit]
}
