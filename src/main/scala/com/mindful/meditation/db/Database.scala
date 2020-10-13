package com.mindful.meditation.db

import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.hikari._
import com.mindful.meditation.config

object Database extends IOApp {

  // Resource yielding a transactor configured with a bounded connect EC and an unbounded
  // transaction EC. Everything will be closed and shut down cleanly after use.
  val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      be <- Blocker[IO] // our blocking EC
      xa <- HikariTransactor.newHikariTransactor[IO](
        config.databaseConnection.driver, // driver classname
        config.databaseConnection.url, // connect URL
        config.databaseConnection.user, // username
        config.databaseConnection.password, // password
        ce, // await connection here
        be // execute JDBC operations here
      )
    } yield xa

  def run(args: List[String]): IO[ExitCode] =
    transactor.use { xa =>
      for {
        n <- sql"select 42".query[Int].unique.transact(xa)
        _ <- IO(println(n))
      } yield ExitCode.Success
    }
}
