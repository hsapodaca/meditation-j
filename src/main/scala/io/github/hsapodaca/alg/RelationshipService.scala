package io.github.hsapodaca.alg

import cats._
import cats.data.EitherT

class RelationshipService[F[_]](
    repository: RelationshipRepositoryAlg[F]
) {
  def create(relationship: EntityRelationship)(implicit
      M: Monad[F]
  ): F[EntityRelationship] = repository.create(relationship)

  def getByEntityId(id: Long)(implicit
      F: Functor[F]
  ): EitherT[F, EntityNotFoundError.type, EntityRelationship] =
    EitherT.fromOptionF(repository.getByEntityId(id), EntityNotFoundError)

  def delete(id: Long): F[Int] = {
    repository.delete(id)
  }
}

object RelationshipService {
  def apply[F[_]](
      repository: RelationshipRepositoryAlg[F]
  ): RelationshipService[F] =
    new RelationshipService[F](repository)
}
