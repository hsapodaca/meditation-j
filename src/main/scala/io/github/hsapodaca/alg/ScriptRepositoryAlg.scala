package io.github.hsapodaca.alg

trait ScriptRepositoryAlg[F[_]] {

  def get(id: Long): F[Option[Script]]

  def list(limit: Int, offset: Int): F[List[Script]]

  def create(script: Script): F[Script]

  def update(
      script: Script
  ): F[Option[Script]]

  def delete(id: Long): F[Int]
}
