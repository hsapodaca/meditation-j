package io.github.hsapodaca.alg

import cats.Applicative
import cats.data.EitherT
import cats.syntax.all._

class EntityValidation[F[_]: Applicative](
    repository: EntityRepositoryAlg[F]
) extends EntityValidationAlg[F] {
  override def doesNotExist(
      entity: Entity
  ): EitherT[F, EntityAlreadyExistsError, Unit] =
    EitherT {
      repository.get(entity.entityName) map {
        case Some(_) => Left(EntityAlreadyExistsError(entity))
        case _       => Right(())
      }
    }
  def entityNameDoesNotExist(
      entity: Entity
  ): EitherT[F, EntityIsInvalidForUpdateError, Unit] = {
    EitherT {
      repository.get(entity.entityName) map {
        case Some(e) if e.id != entity.id =>
          Left(EntityIsInvalidForUpdateError(entity))
        case _ => Right(())
      }
    }
  }
  override def exists(
      entityId: Option[Long]
  ): EitherT[F, EntityNotFoundError.type, Unit] =
    EitherT {
      entityId match {
        case Some(id) =>
          repository.get(id) map {
            case Some(_) => Right(())
            case _       => Left(EntityNotFoundError)
          }
        case _ =>
          Either
            .left[EntityNotFoundError.type, Unit](EntityNotFoundError)
            .pure[F]
      }
    }
}

object EntityValidation {
  def apply[F[_]: Applicative](repository: EntityRepositoryAlg[F]) =
    new EntityValidation[F](repository)
}
