package io.github.hsapodaca.config

import cats.effect.{Async, Blocker, ContextShift, IO, Resource, Sync}
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import io.github.hsapodaca.alg.{EntityValidation, MeditatorService}
import io.github.hsapodaca.repository.EntityRepository
import org.flywaydb.core.Flyway
import io.github.hsapodaca.config._

import scala.concurrent.ExecutionContext

object DatabaseConfig {

  def transactor[F[_]: Async: ContextShift](
      db: DatabaseConnection,
      executionContext: ExecutionContext,
      blocker: Blocker
  )(implicit
      contextShift: ContextShift[IO]
  ): Resource[F, HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](
      db.driver,
      db.url,
      db.user,
      db.password,
      executionContext,
      blocker
    )

  def init[F[_]](db: DatabaseConnection)(implicit S: Sync[F]): F[Unit] =
    S.delay {
      val flyway =
        Flyway.configure().dataSource(db.url, db.user, db.password).load()
      flyway.clean()
      flyway.migrate()
      ()
    }
}
