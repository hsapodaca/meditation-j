package io.github.hsapodaca.alg

import doobie.free.connection.ConnectionIO

trait RelationshipRepositoryAlg[F[_]] {

  def create(r: EntityRelationship): ConnectionIO[Long]

  def delete(id: Long): ConnectionIO[Int]

  def get(id: Long): ConnectionIO[Option[EntityRelationship]]

  def getByEntityId(id: Long): ConnectionIO[Option[EntityRelationship]]

  def list(limit: Int, offset: Int): ConnectionIO[List[EntityRelationship]]

}
