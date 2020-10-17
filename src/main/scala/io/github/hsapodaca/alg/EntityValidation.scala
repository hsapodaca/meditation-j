package io.github.hsapodaca.alg

import cats.Applicative
import cats.data.EitherT
import cats.effect.Bracket
import cats.syntax.all._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor

class EntityValidation[F[_]](
    repository: EntityRepositoryAlg[F],
    transactor: Transactor[F]
)(implicit ev: Bracket[F, Throwable])
    extends EntityValidationAlg[F] {
  override def doesNotExist(
      entity: Entity
  ): EitherT[F, EntityAlreadyExistsError, Unit] = {
    val action = EitherT {
      for {
        e <- repository.get(entity.entityName)
      } yield e match {
        case Some(_) => Left(EntityAlreadyExistsError(entity))
        case _       => Right(())
      }
    }
    action.transact(transactor)
  }

  override def entityNameDoesNotExist(
      entity: Entity
  ): EitherT[F, EntityIsInvalidForUpdateError, Unit] = {
    val action = EitherT {
      for {
        e <- repository.get(entity.entityName)
      } yield e match {
        case Some(e) if e.id != entity.id =>
          Left(EntityIsInvalidForUpdateError(entity))
        case _ => Right(())
      }
    }
    action.transact(transactor)
  }

  override def exists(
      entityId: Option[Long]
  ): EitherT[F, EntityNotFoundError.type, Unit] = {
    EitherT {
      entityId match {
        case Some(id) =>
          val action = for {
            e <- repository.get(id)
          } yield e match {
            case Some(_) => Right(())
            case _       => Left(EntityNotFoundError)
          }
          action.transact(transactor)
        case _ =>
          Either
            .left[EntityNotFoundError.type, Unit](EntityNotFoundError)
            .pure[F]
      }
    }
  }
}

object EntityValidation {
  def apply[F[_]: Applicative](
      repository: EntityRepositoryAlg[F],
      transactor: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]) =
    new EntityValidation[F](repository, transactor)
}
