package io.github.hsapodaca.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.syntax.all._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.{Meditation, MeditationRepositoryAlg}

private object MeditationSQL {

  def select(id: Long): Query0[Meditation] =
    sql"""
    SELECT id, entity_name, summary 
    FROM entities 
    WHERE id = $id and type = 'meditation'
    """.query[Meditation]

  def select(name: String): Query0[Meditation] =
    sql"""
    SELECT id, entity_name, summary 
    FROM entities 
    WHERE entity_name = $name and type = 'meditation'
    """.query[Meditation]

  def select(limit: Int, offset: Int): Query0[Meditation] =
    sql"""
    SELECT id, entity_name, summary 
    FROM entities 
    WHERE type = 'meditation'
    LIMIT ${limit.toLong} OFFSET ${offset.toLong}
    """.query[Meditation]

  def insertValues(m: Meditation): Update0 =
    sql"""
    INSERT INTO entities (entity_name, summary, type)
    VALUES (${m.entityName}, ${m.summary}, 'meditation')
    """.update

  def deleteFrom(id: Long): Update0 =
    sql"""
    DELETE FROM entities
    WHERE id = ${id} and type = 'meditation'
    """.update

  def updateValues(id: Long, m: Meditation): Update0 =
    sql"""
    UPDATE entities
    SET entity_name = ${m.entityName}, summary = ${m.summary} 
    WHERE id = $id and type = 'meditation'
    """.update
}

class MeditationRepository[F[_]](val xa: Transactor[F])(implicit
    ev: Bracket[F, Throwable]
) extends MeditationRepositoryAlg[F] {

  import MeditationSQL._

  override def get(id: Long): F[Option[Meditation]] =
    select(id).option.transact(xa)

  override def get(name: String): F[Option[Meditation]] =
    select(name).option.transact(xa)

  override def list(limit: Int, offset: Int): F[List[Meditation]] = {
    select(limit, offset)
      .to[List]
      .transact(xa)
  }

  override def create(m: Meditation): F[Meditation] =
    insertValues(m)
      .withUniqueGeneratedKeys[Long]("id")
      .map(id => m.copy(id = Some(id)))
      .transact(xa)

  override def update(m: Meditation): F[Option[Meditation]] = {
    OptionT
      .fromOption[ConnectionIO](m.id)
      .semiflatMap { id =>
        updateValues(id, m).run
          .as(m)
      }
      .value
      .transact(xa)
  }

  override def delete(id: Long): F[Int] =
    deleteFrom(id).run.transact(xa)
}

object MeditationRepository {
  def apply[F[_]](
      xa: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]): MeditationRepository[F] =
    new MeditationRepository(xa)
}
