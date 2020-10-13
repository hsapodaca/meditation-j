package com.mindful.meditation.repository

import cats.effect.IO
import com.mindful.meditation.model._
import doobie.util.transactor.Transactor
import fs2.Stream
import doobie._
import doobie.implicits._

class EntityRepository(transactor: Transactor[IO]) {

  def getTherapist(
      id: Long
  ): IO[Either[EntityNotFoundError.type, Therapist]] = {
    sql"SELECT id, entity_name, summary, type FROM entities WHERE id = $id and type = 'therapist'"
      .query[Therapist]
      .option
      .transact(transactor)
      .map {
        case Some(t: Therapist) => Right(t)
        case None               => Left(EntityNotFoundError)
      }
  }

  def getMeditation(
      id: Long
  ): IO[Either[EntityNotFoundError.type, Meditation]] = {
    sql"SELECT id, entity_name, summary, type FROM entities WHERE id = $id and type = 'meditation'"
      .query[Meditation]
      .option
      .transact(transactor)
      .map {
        case Some(t: Meditation) => Right(t)
        case None                => Left(EntityNotFoundError)
      }
  }

  def getTherapists: Stream[IO, Therapist] = {
    sql"SELECT id, entity_name, summary, type FROM entities where type = 'therapist'"
      .query[Therapist]
      .stream
      .transact(transactor)
  }

  def getMeditations: Stream[IO, Meditation] = {
    sql"SELECT id, entity_name, summary, type FROM entities where type = 'meditations'"
      .query[Meditation]
      .stream
      .transact(transactor)
  }

  def createTherapist(entity: Therapist): IO[Therapist] = {
    sql"INSERT INTO entity (entity_name, summary, type) VALUES (${entity.entityName}, ${entity.summary}, 'therapist')".update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(transactor)
      .map { id =>
        entity.copy(id = Some(id))
      }
  }

  def createMeditation(entity: Meditation): IO[Meditation] = {
    sql"INSERT INTO entity (entity_name, summary, type) VALUES (${entity.entityName}, ${entity.summary}, 'meditation')".update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(transactor)
      .map { id =>
        entity.copy(id = Some(id))
      }
  }

  def updateTherapist(
      entity: Therapist
  ): IO[Either[EntityNotFoundError.type, Therapist]] = {
    sql"UPDATE entity SET entity_name = ${entity.entityName}, summary = ${entity.summary} WHERE id = ${entity.id} and type = 'therapist'".update.run
      .transact(transactor)
      .map { rows =>
        if (rows == 1) {
          Right(entity)
        } else {
          Left(EntityNotFoundError)
        }
      }
  }

  def updateMeditation(
      entity: Meditation
  ): IO[Either[EntityNotFoundError.type, Meditation]] = {
    sql"UPDATE entity SET entity_name = ${entity.entityName}, summary = ${entity.summary} WHERE id = ${entity.id} and type = 'meditation'".update.run
      .transact(transactor)
      .map { rows =>
        if (rows == 1) {
          Right(entity)
        } else {
          Left(EntityNotFoundError)
        }
      }
  }

  def deleteEntity(id: Long): IO[Either[EntityNotFoundError.type, Unit]] = {
    sql"DELETE FROM entity WHERE id = $id".update.run.transact(transactor).map {
      affectedRows =>
        if (affectedRows == 1) {
          Right(())
        } else {
          Left(EntityNotFoundError)
        }
    }
  }
}
