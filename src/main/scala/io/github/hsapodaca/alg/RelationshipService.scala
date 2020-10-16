package io.github.hsapodaca.alg

import cats._
import cats.data.EitherT

class RelationshipService[F[_]](
    repository: RelationshipRepositoryAlg[F],
    validation: RelationshipValidationAlg[F]
) {
  def create(relationship: EntityRelationship)(implicit
      M: Monad[F]
  ): EitherT[F, ItemAlreadyExistsError.type, EntityRelationship] =
    for {
      _ <- validation.doesNotExist(relationship)
      saved <- EitherT.liftF(repository.create(relationship))
    } yield saved

  def list(pageSize: Int, offset: Int)(implicit
      F: Functor[F]
  ): F[List[EntityRelationship]] = repository.list(pageSize, offset)

  def listByEntityId(id: Long)(implicit
                              F: Functor[F]
  ): F[List[EntityRelationship]] = repository.listByEntityId(id)

  def get(id: Long)(implicit
      F: Functor[F]
  ): EitherT[F, EntityNotFoundError.type, EntityRelationship] =
    EitherT.fromOptionF(repository.get(id), EntityNotFoundError)

  def delete(id: Long): F[Int] = repository.delete(id)
}

object RelationshipService {
  def apply[F[_]](
      repository: RelationshipRepositoryAlg[F],
      validation: RelationshipValidationAlg[F]
  ): RelationshipService[F] =
    new RelationshipService[F](repository, validation)
}
