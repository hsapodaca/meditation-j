package com.mindfulness.meditation

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]) =
    MeditationServer.stream[IO].compile.drain.as(ExitCode.Success)
}