# Meditation with J

J is someone who has helped me once, when it counted. If you are struggling to focus, they will guide you through a brief mindfulness meditation to help you relax.

## Summary

J is a set of API endpoints for managing and interacting with therapists and interaction scripts.

For more information on usage, please see open-api.yml.

## Getting Started

Bring up local postgres with 

    docker-compose up 

Run tests with

    sbt clean test 
    
## Project overview

- db (database queries)
- model (data models)
- repository (of entities)

- service (business logic)

- web (endpoints, routes)

Also see:

- build.sbt for the specifics on dependencies used
- sql directory for the database schema
- open-api.yml for the endpoints available