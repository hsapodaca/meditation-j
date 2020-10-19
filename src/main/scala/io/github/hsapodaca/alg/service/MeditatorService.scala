package io.github.hsapodaca.alg.service

import cats.data.EitherT
import cats.effect.Bracket
import doobie.Transactor
import doobie.implicits._
import io.github.hsapodaca.alg._

class MeditatorService[F[_]](
    entities: EntityService[F],
    validation: MeditatorValidationAlg[F],
    relationships: RelationshipService[F],
    transactor: Transactor[F]
)(implicit
    ev: Bracket[F, Throwable]
) {
  def create(
      m: Meditator
  ): EitherT[F, MeditatorCreationError, Meditator] = {
    val action = for {
      friendId <- entities.create(m.friend)
      meditationId <- entities.create(m.meditation)
      relationship <- relationships.create(
        EntityRelationship(
          None,
          friendId,
          meditationId,
          EntityRelationshipType.FriendHasMeditation
        )
      )
      friend <- entities.get(friendId)
      meditation <- entities.get(meditationId)
    } yield (friend, meditation, relationship) match {
      case (Some(f), Some(m), Some(_)) => Some(Meditator(f, m))
      case _                           => None
    }

    for {
      _ <- validation.uniqueEntityNames(m)
      _ <- validation.doesNotExist(m)
      r <- EitherT.fromOptionF(
        action.transact(transactor),
        MeditatorAlreadyExistsError: MeditatorCreationError
      )
    } yield r
  }

  def delete(id: Long): EitherT[F, MeditatorNotFoundError.type, Meditator] = {
    val action = for {
      p <- entities.get(id)
      c <- entities.getByParentId(id)
      _ <- entities.delete(id)
      _ <- (p, c) match {
        case (Some(_), Some(c)) if c.id.isDefined =>
          entities.delete(c.id.getOrElse(-1L))
      }
    } yield (p, c) match {
      case (Some(p), Some(c)) => Some(Meditator(p, c))
      case _                  => None
    }
    for {
      _ <- validation.exists(Some(id))
      r <- EitherT.fromOptionF(
        action.transact(transactor),
        MeditatorNotFoundError
      )
    } yield r
  }

  def get(id: Long): F[Option[Meditator]] = {
    val action = for {
      friend <- entities.get(id)
      meditation <- entities.getByParentId(id)
    } yield (friend, meditation) match {
      case (Some(f), Some(m)) => Some(Meditator(f, m))
      case _                  => None
    }
    action.transact(transactor)
  }
}

object MeditatorService {
  def apply[F[_]](
      entities: EntityService[F],
      validation: MeditatorValidationAlg[F],
      relationships: RelationshipService[F],
      transactor: Transactor[F]
  )(implicit
      ev: Bracket[F, Throwable]
  ): MeditatorService[F] =
    new MeditatorService[F](
      entities,
      validation,
      relationships,
      transactor
    )
}
