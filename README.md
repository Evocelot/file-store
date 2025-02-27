# FileStore

`FileStore` is a scalable microservice designed for the efficient central storage of files. Built with a focus on performance and reliability, provides a robust solution for managing file storage in distributed environments. Its architecture ensures scalability, making it suitable for both small-scale applications and large systems. Designed as an independent microservice, `FileStore` can be easily integrated into complex infrastructures, offering a reliable and flexible approach to centralized file management.

The starter project: `springboot - microbase` is an open-source starter project for quickly building `scalable` and `maintainable` Spring Boot-based microservices. For more details: [Evocelot/springboot-microbase](https://github.com/Evocelot/springboot-microbase).

## Technologies used

- Java 21
- SpringBoot 3.4.2
- Docker / Podman
- Make

## Core Functions

- **Upload File** – Supports file uploads with configurable size and chunking via environment variables, optimizing CPU and RAM usage.
- **Get File Details** – Retrieve metadata of uploaded files.
- **Download File** – Download files with adjustable chunking and optional hash verification to ensure data integrity.
- **Delete File** – Remove stored files.

## Prerequisites

To ensure the application functions correctly, you must first `start the database container`. The database will store the metadata required for file storage and retrieval.

To set up the database container properly, the following project will assist you: [Evocelot/file-store-liquibase](https://github.com/Evocelot/file-store-liquibase)

The `file-store-liquibase` project helps start the appropriate database container and create the necessary database schema.

## How to run:

The project includes a `Makefile` to simplify application startup. Each Makefile target can be executed independently.

> **_NOTE:_** If you are using Docker instead of Podman, replace `podman` with `docker` in the Makefile commands.

### Run with full stack

To run the application along with ELK stack and observability features, execute:

```bash
make all
```

This command starts the following containers:

- elasticsearch
- logstash
- kibana
- jaeger
- prometheus
- grafana
- kafka
- zookeeper
- kafka-ui
- sample-module

By default, the file-store-module runs on port `8082`.
The swagger UI can be accessed at: http://localhost:8082/swagger-ui/index.html

### Run the module only

To run only the module only:

```bash
make start-local-container
```

> **_NOTE:_** To disable log collection, tracing and communication via kafka, manually set the `LOGSTASH_ENABLED`,  `TRACING_ENABLED` and `KAFKA_ENABLED` environment variables to `"false"` in the `Makefile`.

## Reaching the files

When starting the service, it is possible to mount the directory where you want to save the uploaded files as a `volume`. This can be configured in the volume definition section of the `Makefile`. Inside the container, the files will be saved in the `/store` directory.

In the current project, you can find the uploaded files in the `/stored-files` folder.

## Docker Images

The released Docker images for this application are available at: [dockerhub](https://hub.docker.com/r/evocelot/file-store)

## Documentation

Detailed documentation is available here: [Documentation](/docs/index.md)

## Contributions

Contributions to the project are welcome! If you find issues or have suggestions for improvements, feel free to open an issue or submit a pull request.
