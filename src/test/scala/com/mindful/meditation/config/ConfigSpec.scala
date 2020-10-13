package com.mindful.meditation.config

import org.specs2.matcher.MatchResult
import com.mindful.meditation.config

class ConfigSpec extends org.specs2.mutable.Specification {

  "Config" >> {
    "load defaults config" >> {
      loadDefaultMeditation()
      loadDefaultTherapist()
    }
    "load db config" >> {
      loadDatabaseConfig()
    }
    "load server config" >> {
      loadServerConfig()
    }
  }

  private[this] def loadDefaultMeditation(): MatchResult[String] =
    config.defaultMeditation.name must beEqualTo(
      "Leaves on a Stream Meditation"
    )

  private[this] def loadDefaultTherapist(): MatchResult[String] =
    config.defaultTherapist.name must beEqualTo("J")

  private[this] def loadDatabaseConfig(): MatchResult[String] =
    config.databaseConnection.user must not beEmpty

  private[this] def loadServerConfig(): MatchResult[String] =
    config.server.host must not beEmpty
}
