package io.github.hsapodaca.alg

case class Meditation(
    id: Option[Long],
    entityName: String,
    summary: String
)

case class Therapist(id: Option[Long], entityName: String, summary: String)

case class Script(
    id: Option[Long],
    entityId: Long,
    script: String
)

// Validation Errors
sealed trait ValidationError extends Product with Serializable
case object MeditationNotFoundError extends ValidationError
case object TherapistNotFoundError extends ValidationError
case object ScriptNotFoundError extends ValidationError
case class MeditationAlreadyExistsError(m: Meditation) extends ValidationError
case class TherapistAlreadyExistsError(t: Therapist) extends ValidationError
case class ScriptAlreadyExistsError(s: Script) extends ValidationError