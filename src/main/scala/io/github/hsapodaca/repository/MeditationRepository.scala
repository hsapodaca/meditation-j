package io.github.hsapodaca.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.syntax.all._
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import io.github.hsapodaca.alg.{MeditationRepositoryAlg, Meditation, Therapist}

class MeditationRepository[F[_]](val xa: Transactor[F])(implicit
    ev: Bracket[F, Throwable]
) extends MeditationRepositoryAlg[F] {

  override def get(
      id: Long
  ): F[Option[Meditation]] = {
    sql"SELECT id, entity_name, summary, type FROM entities WHERE id = $id and type = 'meditation'"
      .query[Meditation]
      .option
      .transact(xa)
  }

  override def get: F[List[Meditation]] = {
    sql"SELECT id, entity_name, summary, type FROM entities where type = 'meditations'"
      .query[Meditation]
      .to[List]
      .transact(xa)
  }

  override def create(entity: Meditation): F[Meditation] = {
    sql"INSERT INTO entity (entity_name, summary, type) VALUES (${entity.entityName}, ${entity.summary}, 'meditation')".update
      .withUniqueGeneratedKeys[Long]("id")
      .map(id => entity.copy(id = Some(id)))
      .transact(xa)
  }

  override def update(
      entity: Meditation
  ): F[Option[Meditation]] = {
    OptionT
      .fromOption[ConnectionIO](entity.id)
      .semiflatMap { id =>
        sql"UPDATE entity SET entity_name = ${entity.entityName}, summary = ${entity.summary} WHERE id = ${id} and type = 'meditation'".update.run
          .as(entity)
      }
      .value
      .transact(xa)
  }

  override def delete(id: Long): F[Int] = {
    sql"DELETE FROM entity WHERE id = $id and type = 'meditation'".update.run
      .transact(xa)
  }
}

object MeditationRepository {
  def apply[F[_]](
      xa: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]): MeditationRepository[F] =
    new MeditationRepository(xa)
}
