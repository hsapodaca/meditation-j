# Meditation with J

J is someone who has helped me once, when it counted. If you are struggling to focus, they will guide you through a brief mindfulness meditation to help you relax.

This is a work in progress, and an attempt to become more comfortable with the cats library and with building and organizing a service from scratch.

## Summary

This project contains a set of API endpoints (made with scala, http4s, circe, doobie, cats) for managing and interacting with someone reciting a meditation script.

For more information on usage, endpoints, please see open-api.yml.

## Getting Started

Bring up local postgres with 

    docker-compose up 

Run tests with

    sbt clean test 
    
## Project overview

- config (application configuration, database config, server config)
- repository (database queries)
- alg (business logic for service layer and validation, data models)
- web (contains routes configuring the endpoints)
- MeditationServer - IOApp object, entrypoint

Data Model:

- Everything is an entity in possession of a script
- Entities (ie, friend, meditation) may have a one way, one-to-one relationship of a certain type
- A meditator is a friend entity that has a relationship with meditation entity

Also see:

- build.sbt for the specifics on dependencies used
- postgresql directory for the database schema, flyway scripts
- open-api.yml for the endpoints available