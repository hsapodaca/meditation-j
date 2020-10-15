package io.github.hsapodaca.alg

import cats._
import cats.data.EitherT

class EntityService[F[_]](
    repository: EntityRepositoryAlg[F],
    validation: EntityValidationAlg[F]
) {
  def create(entity: Entity)(implicit
      M: Monad[F]
  ): EitherT[F, EntityAlreadyExistsError, Entity] =
    for {
      _ <- validation.doesNotExist(entity)
      saved <- EitherT.liftF(repository.create(entity))
    } yield saved

  def update(entity: Entity)(implicit
      M: Monad[F]
  ): EitherT[F, EntityNotFoundError.type, Entity] =
    for {
      _ <- validation.exists(entity.id)
      saved <- EitherT.fromOptionF(
        repository.update(entity),
        EntityNotFoundError
      )
    } yield saved

  def get(id: Long)(implicit
      F: Functor[F]
  ): EitherT[F, EntityNotFoundError.type, Entity] =
    EitherT.fromOptionF(repository.get(id), EntityNotFoundError)

  def delete(id: Long): F[Int] = {
    repository.delete(id)
  }

  def listTherapists(pageSize: Int, offset: Int): F[List[Entity]] = {
    repository.list(EntityType.Therapist, pageSize, offset)
  }

  def listMeditations(pageSize: Int, offset: Int): F[List[Entity]] = {
    repository.list(EntityType.Meditation, pageSize, offset)
  }
}
object EntityService {
  def apply[F[_]](
      repository: EntityRepositoryAlg[F],
      validation: EntityValidationAlg[F]
  ): EntityService[F] =
    new EntityService[F](repository, validation)
}
