package io.github.hsapodaca.alg

import doobie.ConnectionIO

trait EntityRepositoryAlg[F[_]] {

  def get(id: Long): ConnectionIO[Option[Entity]]

  def get(name: String): ConnectionIO[Option[Entity]]

  def list(entityType: EntityType, limit: Int, offset: Int): ConnectionIO[List[Entity]]

  def getByParentId(id: Long): ConnectionIO[Option[Entity]]

  def create(t: Entity): ConnectionIO[Long]

  def update(t: Entity): ConnectionIO[Option[Entity]]

  def delete(id: Long): ConnectionIO[Int]
}
