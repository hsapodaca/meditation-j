# Meditation with J

## Overview

J is someone who has helped me when it counted. If you are struggling to regain focus, they will guide you through a brief mindfulness meditation to help you relax. (Front-end forthcoming.)

This is an evolving attempt to grow comfortable with the cats library and with building and maintaining a proof-of-concept service from scratch using Scala, functional programming libraries.

## Summary

The project contains a set of API endpoints ([Scala](https://scala-lang.org/), [http4s](https://http4s.org/), [circe](https://github.com/circe/circe), [doobie](https://github.com/tpolecat/doobie), [cats](https://typelevel.org/cats/)) for managing and interacting with someone reciting a meditation script.

## Getting Started

Clone or download locally.

Bring up local postgres with 

    docker-compose up 

And then run tests with

    sbt clean test 
    
Or launch on localhost:8080

    sbt run 

You will be able to see:

    curl http://localhost:8080/status | jq
    {
      "status": "Up",
      "meditation": "Leaves on a Stream Meditation",
      "friend": "J"
    }    
    
Meet J: 

    curl http://localhost:8080/v1/meditators/1 | jq

## Endpoints

* GET `/status`
* GET `/v1/entities/${id}`
* GET `/v1/entities/friends`
* GET `/v1/entities/friends?pageSize=5&offset=0`
* GET `/v1/entities/meditations`
* GET `/v1/entities/meditations?pageSize=5&offset=0`
* GET `/v1/entities/${id}/relationships`
* GET `/v1/relationships/${id}`
* GET `/v1/meditators/${id}`

* PUT `/v1/entities/id`
* POST `/v1/meditators`
* DELETE `/v1/meditators`
    
## Project Overview

- **alg** - all business logic resides here. Service layer, domain-specific things covered by custom code, data validations, models.
- **repository** - all sql - postgres queries without transaction control, which is owned by Service layer. Exceptions are: seeded data via flyway. Also note the cascade delete logic on relationships table.
- **web** - endpoint routes, json encoding/decoding, error codes.
- **config** - application, database (doobie transactor), server configuration.
- **test** - Basic ScalaTest for now.
- **MeditationServer** - IOApp object, entry point.

## Data Model
### Summary
- Everything is an entity with a script.
- Pairs of entities have one-directional relationships. (Meditator = friend -> meditation)

## Files of Interest

- build.sbt - dependencies used.
- postgresql, flyway scripts for the database schema in resources/db.migration.
- MeditatorService - complexity handled within single transaction.

### Notes
- doobie-scalatest tests under `repository` in tests  - check sql, `endpoint` tests test endpoints.
- Service layer has control over transactions.
- Readiness check endpoint depends on database and seeded data.
- Test coverage reports via scoverage plugin. '[80% and no less](https://www.artima.com/weblogs/viewpost.jsp?thread=204677)'.

## References
- [http4s template](https://http4s.org/v0.21/)
- [doobie docs](https://tpolecat.github.io/doobie/index.html) 
- [influenced by this](https://github.com/pauljamescleary/scala-pet-store)
