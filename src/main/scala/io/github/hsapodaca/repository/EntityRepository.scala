package io.github.hsapodaca.repository

import cats.data.OptionT
import cats.syntax.all._
import doobie._
import doobie.implicits._
import io.github.hsapodaca.alg.{Entity, EntityRepositoryAlg, EntityType, MeditatorValidationAlg}

private object EntitySQL {

  def select(id: Long): Query0[Entity] =
    sql"""
    SELECT id, name, summary, script, type
    FROM entities
    WHERE id = $id
    """.query[Entity]

  def select(name: String): Query0[Entity] =
    sql"""
    SELECT id, name, summary, script, type
    FROM entities
    WHERE name = $name
    """.query[Entity]

  def select(entityType: EntityType, limit: Int, offset: Int): Query0[Entity] =
    sql"""
    SELECT id, name, summary, script, type
    FROM entities
    WHERE type = ${entityType}
    LIMIT ${limit.toLong} OFFSET ${offset.toLong}
    """.query[Entity]

  def selectByParentId(id: Long): Query0[Entity] =
    sql"""
    SELECT e.id, e.name, e.summary, e.script, e.type
    FROM entity_relationships er JOIN entities e on er.target_entity_id = e.id
    WHERE er.primary_entity_id = $id
    """.query[Entity]

  def insertValues(m: Entity): Update0 =
    sql"""
    INSERT INTO entities (name, summary, script, type)
    VALUES (${m.name}, ${m.summary}, ${m.script}, ${m.`type`})
    """.update

  def deleteFrom(id: Long): Update0 =
    sql"""
    DELETE FROM entities
    WHERE id = $id
    """.update

  def updateValues(id: Long, m: Entity): Update0 =
    sql"""
    UPDATE entities
    SET name = ${m.name}, summary = ${m.summary}, script = ${m.script}
    WHERE id = $id
    """.update
}

class EntityRepository[F[_]] extends EntityRepositoryAlg[F] {

  import EntitySQL._

  override def get(id: Long): ConnectionIO[Option[Entity]] = select(id).option

  override def get(name: String): ConnectionIO[Option[Entity]] =
    select(name).option

  override def list(
                     entityType: EntityType,
                     limit: Int,
                     offset: Int
                   ): ConnectionIO[List[Entity]] = {
    select(entityType, limit, offset)
      .to[List]
  }

  override def getByParentId(
                              id: Long
                            ): ConnectionIO[Option[Entity]] = {
    selectByParentId(id).option
  }

  override def create(m: Entity): ConnectionIO[Long] =
    insertValues(m)
      .withUniqueGeneratedKeys[Long]("id")

  override def update(m: Entity): ConnectionIO[Option[Entity]] =
    OptionT
      .fromOption[ConnectionIO](m.id)
      .semiflatMap { id =>
        updateValues(id, m).run
          .as(m)
      }
      .value

  override def delete(id: Long): ConnectionIO[Int] = deleteFrom(id).run
}

object EntityRepository {
  def apply[F[_]]: EntityRepository[F] = new EntityRepository()
}
