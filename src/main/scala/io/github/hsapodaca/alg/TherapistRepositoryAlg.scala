package io.github.hsapodaca.alg

trait TherapistRepositoryAlg[F[_]] {

  def get(id: Long): F[Option[Therapist]]

  def get: F[List[Therapist]]

  def create(entity: Therapist): F[Therapist]

  def update(entity: Therapist): F[Option[Therapist]]

  def delete(id: Long): F[Int]
}
