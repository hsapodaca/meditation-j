package io.github.hsapodaca.alg

import cats._
import cats.data.EitherT

class EntityService[F[_]](
    repository: EntityRepositoryAlg[F],
    validation: EntityValidationAlg[F]
) {

  def create(entity: Entity)(implicit
      M: Monad[F]
  ): EitherT[F, ItemAlreadyExistsError.type, Entity] =
    for {
      _ <- validation.doesNotExist(entity)
      saved <- EitherT.liftF(repository.create(entity))
    } yield saved

  def update(entity: Entity)(implicit
      M: Monad[F]
  ): EitherT[F, EntityIsInvalidForUpdateError, Entity] =
    for {
      _ <- validation.entityNameDoesNotExist(entity)
      saved <- EitherT.fromOptionF(
        repository.update(entity),
        EntityIsInvalidForUpdateError(entity)
      )
    } yield saved

  def get(id: Long)(implicit
      F: Functor[F]
  ): EitherT[F, EntityNotFoundError.type, Entity] =
    EitherT.fromOptionF(repository.get(id), EntityNotFoundError)

  def getByParentId(id: Long)(implicit
      F: Functor[F]
  ): EitherT[F, EntityNotFoundError.type, Entity] =
    EitherT.fromOptionF(repository.getByParentId(id), EntityNotFoundError)

  def delete(id: Long): F[Int] = repository.delete(id)

  def listFriends(pageSize: Int, offset: Int): F[List[Entity]] = {
    repository.list(EntityType.Friend, pageSize, offset)
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
