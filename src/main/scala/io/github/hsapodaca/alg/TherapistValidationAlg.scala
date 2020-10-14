//package io.github.hsapodaca.alg
//
//import cats.data.EitherT
//
//trait TherapistValidationAlg[F[_]] {
//  def doesNotExist(
//                    therapist: Therapist
//                  ): EitherT[F, TherapistAlreadyExistsError, Unit]
//
//  def exists(
//              therapistId: Option[Long]
//            ): EitherT[F, TherapistNotFoundError.type, Unit]
//
//}
