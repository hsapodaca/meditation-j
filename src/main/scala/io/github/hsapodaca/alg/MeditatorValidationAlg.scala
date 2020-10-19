package io.github.hsapodaca.alg
import cats.data.EitherT

trait MeditatorValidationAlg[F[_]] {

  def doesNotExist(
      meditator: Meditator
  ): EitherT[F, MeditatorAlreadyExistsError.type, Unit]

  def uniqueEntityNames(
      meditator: Meditator
  ): EitherT[F, MeditatorEntityNamesMatchError.type, Unit]

  def exists(
      id: Option[Long]
  ): EitherT[F, MeditatorNotFoundError.type, Unit]

}
