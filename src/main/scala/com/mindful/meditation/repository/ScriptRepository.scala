package com.mindful.Script.repository

import cats.effect.IO
import com.mindful.meditation.model._
import com.mindful.meditation.model.ScriptNotFoundError
import doobie.implicits._
import doobie.util.transactor.Transactor
import fs2.Stream

class ScriptRepository(transactor: Transactor[IO]) {

  def getScript(
      id: Long
  ): IO[Either[ScriptNotFoundError.type, Script]] = {
    sql"SELECT id, entity_id, script FROM scripts WHERE id = $id"
      .query[Script]
      .option
      .transact(transactor)
      .map {
        case Some(e) => Right(e)
        case None    => Left(ScriptNotFoundError)
      }
  }

  def getScripts: Stream[IO, Script] = {
    sql"SELECT id, entity_id, script FROM scripts"
      .query[Script]
      .stream
      .transact(transactor)
  }

  def createScript(script: Script): IO[Int] = {
    sql"INSERT INTO Script (entity_id, script) VALUES (${script.id},${script.entityId}, ${script.script})".update.run
      .transact(transactor)
  }

  def updateScript(
      script: Script
  ): IO[Either[ScriptNotFoundError.type, Script]] = {
    sql"UPDATE script SET script_name = ${script.entityId}, summary = ${script.script} WHERE id = ${script.id}".update.run
      .transact(transactor)
      .map { rows =>
        if (rows == 1) {
          Right(script)
        } else {
          Left(ScriptNotFoundError)
        }
      }
  }

  def deleteScript(id: Long): IO[Either[ScriptNotFoundError.type, Unit]] = {
    sql"DELETE FROM script WHERE id = $id".update.run.transact(transactor).map {
      affectedRows =>
        if (affectedRows == 1) {
          Right(())
        } else {
          Left(ScriptNotFoundError)
        }
    }
  }
}
