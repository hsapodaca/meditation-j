package io.github.hsapodaca.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.syntax.all._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.{Entity, EntityRepositoryAlg, EntityType}

private object EntitySQL {

  def select(id: Long): Query0[Entity] =
    sql"""
    SELECT id, entity_name, summary, script, type
    FROM entities
    WHERE id = $id
    """.query[Entity]

  def select(name: String): Query0[Entity] =
    sql"""
    SELECT id, entity_name, summary, script, type
    FROM entities
    WHERE entity_name = $name
    """.query[Entity]

  def select(entityType: EntityType, limit: Int, offset: Int): Query0[Entity] =
    sql"""
    SELECT id, entity_name, summary, script, type
    FROM entities
    WHERE type = ${entityType}
    LIMIT ${limit.toLong} OFFSET ${offset.toLong}
    """.query[Entity]

  def insertValues(m: Entity): Update0 =
    sql"""
    INSERT INTO entities (entity_name, summary, script, type)
    VALUES (${m.entityName}, ${m.summary}, ${m.script}, ${m.`type`})
    """.update

  def deleteFrom(id: Long): Update0 =
    sql"""
    DELETE FROM entities
    WHERE id = $id
    """.update

  def updateValues(id: Long, m: Entity): Update0 =
    sql"""
    UPDATE entities
    SET entity_name = ${m.entityName}, summary = ${m.summary}
    WHERE id = $id
    """.update
}

class EntityRepository[F[_]](val xa: Transactor[F])(implicit
    ev: Bracket[F, Throwable]
) extends EntityRepositoryAlg[F] {

  import EntitySQL._

  override def get(id: Long): F[Option[Entity]] =
    select(id).option.transact(xa)

  override def get(name: String): F[Option[Entity]] =
    select(name).option.transact(xa)

  override def list(entityType: EntityType, limit: Int, offset: Int): F[List[Entity]] = {
    select(entityType, limit, offset)
      .to[List]
      .transact(xa)
  }

  override def create(m: Entity): F[Entity] =
    insertValues(m)
      .withUniqueGeneratedKeys[Long]("id")
      .map(id => m.copy(id = Some(id)))
      .transact(xa)

  override def update(m: Entity): F[Option[Entity]] =
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

object EntityRepository {
  def apply[F[_]](
      xa: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]): EntityRepository[F] =
    new EntityRepository(xa)
}
