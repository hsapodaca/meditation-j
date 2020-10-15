package io.github.hsapodaca.alg

trait EntityRepositoryAlg[F[_]] {

  def get(id: Long): F[Option[Entity]]

  def get(name: String): F[Option[Entity]]

  def list(entityType: EntityType, limit: Int, offset: Int): F[List[Entity]]

  def create(t: Entity): F[Entity]

  def update(t: Entity): F[Option[Entity]]

  def delete(id: Long): F[Int]
}
