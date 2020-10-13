package com.mindful.meditation.db

import cats.effect.{Blocker, ContextShift, IO, Resource}
import doobie.hikari._
import com.mindful.meditation.config.DatabaseConnection
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

object Database {

  def transactor(
      db: DatabaseConnection,
      executionContext: ExecutionContext,
      blocker: Blocker
  )(implicit
      contextShift: ContextShift[IO]
  ): Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      db.driver,
      db.url,
      db.user,
      db.password,
      executionContext,
      blocker
    )

  def init(hikariTransactor: HikariTransactor[IO]): IO[Unit] =
    hikariTransactor.configure { dataSource =>
      IO {
        val flyway = Flyway.configure().dataSource(dataSource).load()
        flyway.migrate()
        ()
      }
    }
}
