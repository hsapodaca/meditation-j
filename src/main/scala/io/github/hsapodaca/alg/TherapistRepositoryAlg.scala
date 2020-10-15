package io.github.hsapodaca.alg

trait TherapistRepositoryAlg[F[_]] {

  def get(id: Long): F[Option[Therapist]]

  def get(name: String): F[Option[Therapist]]

  def list(limit: Int, offset: Int): F[List[Therapist]]

  def create(t: Therapist): F[Therapist]

  def update(t: Therapist): F[Option[Therapist]]

  def delete(id: Long): F[Int]
}
