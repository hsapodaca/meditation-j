package io.github.hsapodaca.alg

trait RelationshipRepositoryAlg[F[_]] {

  def listByEntityId(id: Long): F[List[EntityRelationship]]

  def get(id: Long): F[Option[EntityRelationship]]

  def list(limit: Int, offset: Int): F[List[EntityRelationship]]

  def create(script: EntityRelationship): F[EntityRelationship]

  def delete(id: Long): F[Int]
}
