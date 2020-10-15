package io.github.hsapodaca.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.syntax.all._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.{Therapist, TherapistRepositoryAlg}

private object TherapistSQL {

  def select(id: Long): Query0[Therapist] =
    sql"""
    SELECT id, entity_name, summary
    FROM entities
    WHERE id = $id and type = 'therapist'
    """.query[Therapist]

  def select(name: String): Query0[Therapist] =
    sql"""
    SELECT id, entity_name, summary
    FROM entities
    WHERE entity_name = $name and type = 'therapist'
    """.query[Therapist]

  def select(limit: Int, offset: Int): Query0[Therapist] =
    sql"""
    SELECT id, entity_name, summary
    FROM entities
    WHERE type = 'therapist'
    LIMIT ${limit.toLong} OFFSET ${offset.toLong}
    """.query[Therapist]

  def insertValues(m: Therapist): Update0 =
    sql"""
    INSERT INTO entities (entity_name, summary, type)
    VALUES (${m.entityName}, ${m.summary}, 'therapist')
    """.update

  def deleteFrom(id: Long): Update0 =
    sql"""
    DELETE FROM entities
    WHERE id = $id and type = 'therapist'
    """.update

  def updateValues(id: Long, m: Therapist): Update0 =
    sql"""
    UPDATE entities
    SET entity_name = ${m.entityName}, summary = ${m.summary}
    WHERE id = $id and type = 'therapist'
    """.update
}

class TherapistRepository[F[_]](val xa: Transactor[F])(implicit
    ev: Bracket[F, Throwable]
) extends TherapistRepositoryAlg[F] {

  import TherapistSQL._

  override def get(id: Long): F[Option[Therapist]] =
    select(id).option.transact(xa)

  override def get(name: String): F[Option[Therapist]] =
    select(name).option.transact(xa)

  override def list(limit: Int, offset: Int): F[List[Therapist]] = {
    select(limit, offset)
      .to[List]
      .transact(xa)
  }

  override def create(m: Therapist): F[Therapist] =
    insertValues(m)
      .withUniqueGeneratedKeys[Long]("id")
      .map(id => m.copy(id = Some(id)))
      .transact(xa)

  override def update(m: Therapist): F[Option[Therapist]] =
    OptionT
      .fromOption[ConnectionIO](m.id)
      .semiflatMap { id =>
        updateValues(id, m).run
          .as(m)
      }
      .value
      .transact(xa)

  override def delete(id: Long): F[Int] = deleteFrom(id).run.transact(xa)
}

object TherapistRepository {
  def apply[F[_]](
      xa: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]): TherapistRepository[F] =
    new TherapistRepository(xa)
}
