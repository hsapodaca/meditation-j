package io.github.hsapodaca.alg.service

import cats.data.EitherT
import cats.effect.Bracket
import doobie.Transactor
import doobie.implicits._
import io.github.hsapodaca.alg.{
  EntityRelationship,
  EntityRelationshipType,
  Meditator,
  MeditatorAlreadyExistsError,
  MeditatorNotFoundError,
  MeditatorValidationAlg
}

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
  ): EitherT[F, MeditatorAlreadyExistsError.type, Meditator] = {
    val action = for {
      friendId <- entities.create(m.friend)
      meditationId <- entities.create(m.meditation)
      _ <- relationships.create(
        EntityRelationship(
          None,
          friendId,
          meditationId,
          EntityRelationshipType.FriendHasMeditation
        )
      )
      friend <- entities.get(friendId)
      meditation <- entities.get(meditationId)
    } yield (friend, meditation) match {
      case (Some(f), Some(m)) => Some(Meditator(f, m))
      case _                  => None
    }
    for {
      _ <- validation.doesNotExist(m)
      r <- EitherT.fromOptionF(
        action.transact(transactor),
        MeditatorAlreadyExistsError
      )
    } yield r
  }

  def delete(id: Long): EitherT[F, MeditatorNotFoundError.type, Meditator] = {
    val action = for {
      p <- entities.get(id)
      c <- entities.getByParentId(id)
      _ <- entities.delete(id)
      _ <- c match {
        case Some(e) if e.id.isDefined => entities.delete(e.id.getOrElse(-1L))
      }
    } yield (p, c) match {
      case (Some(p), Some(c)) => Some(Meditator(p, c))
      case _                  => None
    }
    EitherT.fromOptionF(action.transact(transactor), MeditatorNotFoundError)
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
