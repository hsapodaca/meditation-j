# Meditation with J

J is someone who has helped me once, when it counted. If you are struggling to focus, they will guide you through a brief mindfulness meditation to help you relax.

## Summary

J is a set of API endpoints for managing and interacting interaction scripts for various virtual therapists reciting a meditation script.

For more information on usage, endpoints, please see open-api.yml.

## Getting Started

Bring up local postgres with 

    docker-compose up 

Run tests with

    sbt clean test 
    
## Project overview

- config (application configuration, database config, server config)
- repository (database queries)
- service (service layer between repositories and endpoints, business logic)
- alg (business logic and data models)
- endpoint (web layer, contains routes configuring the endpoints)
- MeditationServer - IOApp object, entrypoint

Also see:

- build.sbt for the specifics on dependencies used
- postgresql directory for the database schema, flyway scripts
- open-api.yml for the endpoints available