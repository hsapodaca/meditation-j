package io.github.hsapodaca.repository

import doobie.free.connection._
import doobie.implicits._
import doobie.{Query0, Update0}
import io.github.hsapodaca.alg.{EntityRelationship, RelationshipRepositoryAlg}

private object RelationshipSQL {

  def selectByEntityId(id: Long): Query0[EntityRelationship] =
    sql"""
    SELECT id, primary_entity_id, target_entity_id, type
    FROM entity_relationships
    WHERE primary_entity_id = $id
    """.query[EntityRelationship]

  def select(id: Long): Query0[EntityRelationship] =
    sql"""
    SELECT id, primary_entity_id, target_entity_id, type
    FROM entity_relationships
    WHERE id = $id
    """.query[EntityRelationship]

  def select(limit: Int, offset: Int): Query0[EntityRelationship] =
    sql"""
    SELECT id, primary_entity_id, target_entity_id, type
    FROM entity_relationships
    LIMIT ${limit.toLong} OFFSET ${offset.toLong}
    """.query[EntityRelationship]

  def insertValues(r: EntityRelationship): Update0 =
    sql"""
    INSERT INTO entity_relationships (primary_entity_id, target_entity_id, type)
    VALUES (${r.primaryEntityId}, ${r.targetEntityId}, ${r.`type`})
    """.update

  def deleteFrom(id: Long): Update0 =
    sql"""
    DELETE FROM entity_relationships
    WHERE id = $id
    """.update
}

class RelationshipRepository[F[_]] extends RelationshipRepositoryAlg[F] {

  import RelationshipSQL._

  override def get(id: Long): ConnectionIO[Option[EntityRelationship]] =
    select(id).option

  override def getByEntityId(
      id: Long
  ): ConnectionIO[Option[EntityRelationship]] =
    selectByEntityId(id).option

  override def list(
      limit: Int,
      offset: Int
  ): ConnectionIO[List[EntityRelationship]] =
    select(limit, offset).to[List]

  override def create(r: EntityRelationship): ConnectionIO[Long] =
    insertValues(r).withUniqueGeneratedKeys[Long]("id")

  override def delete(id: Long): ConnectionIO[Int] = deleteFrom(id).run
}

object RelationshipRepository {
  def apply[F[_]]: RelationshipRepository[F] = new RelationshipRepository()
}
