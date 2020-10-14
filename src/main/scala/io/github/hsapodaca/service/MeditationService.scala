package io.github.hsapodaca.service

import cats._
import cats.data.EitherT
import io.github.hsapodaca.alg.{Meditation, MeditationAlreadyExistsError, MeditationNotFoundError, MeditationRepositoryAlg}

class MeditationService[F[_]](repository: MeditationRepositoryAlg[F]) {
  def create(Meditation: Meditation)(implicit
      M: Monad[F]
  ): EitherT[F, MeditationAlreadyExistsError, Meditation] =
    for {
      //_ <- validation.doesNotExist(Meditation)
      saved <- EitherT.liftF(repository.create(Meditation))
    } yield saved

  def update(Meditation: Meditation)(implicit
      M: Monad[F]
  ): EitherT[F, MeditationNotFoundError.type, Meditation] =
    for {
      //_ <- validation.exists(Meditation.id)
      saved <- EitherT.fromOptionF(
        repository.update(Meditation),
        MeditationNotFoundError
      )
    } yield saved

  def get(id: Long)(implicit
      F: Functor[F]
  ): EitherT[F, MeditationNotFoundError.type, Meditation] =
    EitherT.fromOptionF(repository.get(id), MeditationNotFoundError)

  def delete(id: Long)(implicit F: Functor[F]): F[Int] = {
    repository.delete(id)
  }

}
object MeditationService {
  def apply[F[_]](
      repository: MeditationRepositoryAlg[F]
  ): MeditationService[F] =
    new MeditationService[F](repository)
}
