package io.github.hsapodaca.alg

trait MeditationRepositoryAlg[F[_]] {

  def get(id: Long): F[Option[Meditation]]

  def get(name: String): F[Option[Meditation]]

  def list(limit: Int, offset: Int): F[List[Meditation]]

  def create(entity: Meditation): F[Meditation]

  def update(entity: Meditation): F[Option[Meditation]]

  def delete(id: Long): F[Int]
}
