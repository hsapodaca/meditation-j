package io.github.hsapodaca.alg

import io.github.hsapodaca.config

case class Meditation(
    id: Option[Long],
    entityName: String,
    summary: String,
    scriptId: Long
)

case class Therapist(
    id: Option[Long],
    entityName: String,
    summary: String,
    scriptId: Long
)

case class Script(
    id: Option[Long],
    script: String
)

case class StatusInfo(
    status: String = "UP",
    defaultMeditation: String = config.defaultMeditation.name,
    defaultTherapist: String = config.defaultTherapist.name
)

// Validation Errors
sealed trait ValidationError extends Product with Serializable
case object MeditationNotFoundError extends ValidationError
case object TherapistNotFoundError extends ValidationError
case object ScriptNotFoundError extends ValidationError
case class MeditationAlreadyExistsError(m: Meditation) extends ValidationError
case class TherapistAlreadyExistsError(t: Therapist) extends ValidationError
case class ScriptAlreadyExistsError(s: Script) extends ValidationError
