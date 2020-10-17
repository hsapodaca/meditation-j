package io.github.hsapodaca.endpoint

import cats.effect.{Blocker, ConcurrentEffect, ContextShift, IO, Resource, Timer, _}
import cats.implicits._
import doobie.util.ExecutionContexts
import io.github.hsapodaca.alg.service.{EntityService, RelationshipService, MeditatorService}
import io.github.hsapodaca.alg.{EntityValidation}
import io.github.hsapodaca.config
import io.github.hsapodaca.config.DatabaseConfig
import io.github.hsapodaca.repository.{EntityRepository, RelationshipRepository}
import io.github.hsapodaca.web.{EntityEndpoints, MeditatorEndpoints, ReadinessCheckEndpoints, RelationshipEndpoints}
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.{Server => H4Server}

object EntityServer extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    createServer.use(_ => IO.never).as(ExitCode.Success)

  def createServer[F[_]: ContextShift: ConcurrentEffect: Timer]
      : Resource[F, H4Server[F]] = {

    for {
      serverEc <- ExecutionContexts.cachedThreadPool[F]
      connEc <-
        ExecutionContexts.fixedThreadPool[F](config.databaseConnection.poolSize)
      txnEc <- ExecutionContexts.cachedThreadPool[F]
      xa <- DatabaseConfig.transactor(
        config.databaseConnection,
        connEc,
        Blocker.liftExecutionContext(txnEc)
      )
      entityRepo = EntityRepository[F]
      entityValidation = EntityValidation[F](entityRepo, xa)
      entityAlg = EntityService[F](entityRepo, entityValidation, xa)
      relationshipRepo = RelationshipRepository[F]
      relationshipAlg = RelationshipService[F](relationshipRepo, xa)
      meditatorAlg = MeditatorService[F](entityAlg, relationshipAlg, xa)
      httpApp = (
          ReadinessCheckEndpoints.endpoints[F](meditatorAlg) <+>
            EntityEndpoints.endpoints[F](entityAlg) <+>
            RelationshipEndpoints.endpoints[F](relationshipAlg) <+>
            MeditatorEndpoints.endpoints[F](meditatorAlg)
      ).orNotFound
      _ <- Resource.liftF(DatabaseConfig.init(config.databaseConnection))
      server <- BlazeServerBuilder[F](serverEc)
        .bindHttp(config.server.port, config.server.host)
        .withHttpApp(httpApp)
        .resource
    } yield server
  }
}
