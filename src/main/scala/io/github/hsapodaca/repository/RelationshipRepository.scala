package io.github.hsapodaca.repository

import cats.effect.Bracket
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
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

class RelationshipRepository[F[_]](val xa: Transactor[F])(implicit
    ev: Bracket[F, Throwable]
) extends RelationshipRepositoryAlg[F] {

  import RelationshipSQL._

  override def get(id: Long): F[Option[EntityRelationship]] =
    select(id).option.transact(xa)

  override def getByEntityId(id: Long): F[Option[EntityRelationship]] =
    selectByEntityId(id).option.transact(xa)

  override def list(limit: Int, offset: Int): F[List[EntityRelationship]] =
    select(limit, offset).to[List].transact(xa)

  override def create(
      relationship: EntityRelationship
  ): F[EntityRelationship] = {
    insertValues(relationship)
      .withUniqueGeneratedKeys[Long]("id")
      .map(id => relationship.copy(id = Some(id)))
      .transact(xa)
  }

  override def delete(id: Long): F[Int] = {
    deleteFrom(id).run.transact(xa)
  }
}

object RelationshipRepository {
  def apply[F[_]](
      xa: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]): RelationshipRepository[F] =
    new RelationshipRepository(xa)
}
