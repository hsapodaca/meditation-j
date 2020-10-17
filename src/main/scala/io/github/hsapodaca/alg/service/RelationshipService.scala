package io.github.hsapodaca.alg.service

import cats.effect.Bracket
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.{EntityRelationship, RelationshipRepositoryAlg}

class RelationshipService[F[_]](
    repository: RelationshipRepositoryAlg[F],
    transactor: Transactor[F]
)(implicit ev: Bracket[F, Throwable]) {

  def create(
      r: EntityRelationship
  ): ConnectionIO[Option[EntityRelationship]] = {
    for {
      i <- repository.create(r)
      r <- repository.get(i)
    } yield r
  }

  def delete(id: Long): ConnectionIO[Int] = repository.delete(id)

  def get(id: Long): ConnectionIO[Option[EntityRelationship]] =
    repository.get(id)

  def getAndTransact(id: Long): F[Option[EntityRelationship]] =
    repository.get(id).transact(transactor)

  def getByEntityIdAndTransact(id: Long): F[Option[EntityRelationship]] =
    repository.getByEntityId(id).transact(transactor)

  def listAndTransact(pageSize: Int, offset: Int): F[List[EntityRelationship]] =
    repository.list(pageSize, offset).transact(transactor)

}

object RelationshipService {
  def apply[F[_]](
      repository: RelationshipRepositoryAlg[F],
      transactor: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]): RelationshipService[F] =
    new RelationshipService[F](repository, transactor)
}
