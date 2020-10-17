package io.github.hsapodaca.alg.service

import cats.Monad
import cats.data.EitherT
import cats.effect.Bracket
import cats.free.Free
import doobie.free.connection
import doobie.{Transactor, _}
import doobie.implicits._
import io.github.hsapodaca.alg.{
  Entity,
  EntityNotFoundError,
  EntityRepositoryAlg,
  EntityType,
  EntityValidation
}

class EntityService[F[_]](
    repository: EntityRepositoryAlg[F],
    validation: EntityValidation[F],
    transactor: Transactor[F]
)(implicit ev: Bracket[F, Throwable]) {

  def get(id: Long): ConnectionIO[Option[Entity]] =
    repository.get(id)

  def getByParentId(id: Long): ConnectionIO[Option[Entity]] =
    repository.getByParentId(id)

  def getAndTransact(id: Long): F[Option[Entity]] =
    repository.get(id).transact(transactor)

  def getByParentIdAndTransact(id: Long): F[Option[Entity]] =
    repository.get(id).transact(transactor)

  def listFriends(pageSize: Int, offset: Int): F[List[Entity]] =
    repository.list(EntityType.Friend, pageSize, offset).transact(transactor)

  def listMeditations(pageSize: Int, offset: Int): F[List[Entity]] =
    repository
      .list(EntityType.Meditation, pageSize, offset)
      .transact(transactor)

  def create(e: Entity): ConnectionIO[Long] = repository.create(e)

  def delete(id: Long): ConnectionIO[Int] = repository.delete(id)

  def deleteAndTransact(id: Long): F[Int] =
    repository.delete(id).transact(transactor)

  def update(e: Entity): ConnectionIO[Option[Entity]] =
    repository.update(e)

  def updateAndTransact(id: Long, e: Entity)(implicit
      M: Monad[F]
  ): EitherT[F, EntityNotFoundError.type, Entity] = {
    val action: Free[connection.ConnectionOp, Option[Entity]] = for {
      _ <- repository.update(e)
      r <- repository.get(id)
    } yield r
    for {
      _ <- validation.exists(e.id)
      r <- EitherT.fromOptionF(action.transact(transactor), EntityNotFoundError)
    } yield r
  }
}

object EntityService {
  def apply[F[_]](
      repository: EntityRepositoryAlg[F],
      validation: EntityValidation[F],
      transactor: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]): EntityService[F] =
    new EntityService[F](repository, validation, transactor)
}
