package io.github.hsapodaca.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.syntax.all._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.{Meditation, MeditationRepositoryAlg, Therapist, TherapistRepositoryAlg}

class TherapistRepository[F[_]](val xa: Transactor[F])(implicit
    ev: Bracket[F, Throwable]
) extends TherapistRepositoryAlg[F] {

  override def get(
      id: Long
  ): F[Option[Therapist]] = {
    sql"SELECT id, entity_name, summary, type FROM entities WHERE id = $id and type = 'therapist'"
      .query[Therapist]
      .option
      .transact(xa)
  }

  override def get: F[List[Therapist]] = {
    sql"SELECT id, entity_name, summary, type FROM entities where type = 'therapist'"
      .query[Therapist]
      .to[List]
      .transact(xa)
  }

  override def create(entity: Therapist): F[Therapist] = {
    sql"INSERT INTO entity (entity_name, summary, type) VALUES (${entity.entityName}, ${entity.summary}, 'therapist')".update
      .withUniqueGeneratedKeys[Long]("id")
      .map(id => entity.copy(id = Some(id)))
      .transact(xa)
  }

  override def update(
      entity: Therapist
  ): F[Option[Therapist]] =
    OptionT
      .fromOption[ConnectionIO](entity.id)
      .semiflatMap { id =>
        sql"UPDATE entity SET entity_name = ${entity.entityName}, summary = ${entity.summary} WHERE id = ${id} and type = 'therapist'".update.run
          .as(entity)
      }
      .value
      .transact(xa)

  override def delete(id: Long): F[Int] = {
    sql"DELETE FROM entity WHERE id = $id and type = 'therapist'".update.run
      .transact(xa)
  }
}

object TherapistRepository {
  def apply[F[_]](
      xa: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]): TherapistRepository[F] =
    new TherapistRepository(xa)
}
