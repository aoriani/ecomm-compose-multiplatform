# ecomm

This project is an e-commerce backend application built with Ktor. It provides a GraphQL API for managing products and other e-commerce functionalities.

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                                     | Description                                |
| ----------------------------------------------------------|-------------------------------------------- |
| [Routing](https://start.ktor.io/p/routing)               | Provides a structured routing DSL          |
| [Static Content](https://start.ktor.io/p/static-content) | Serves static files from defined locations |
| GraphQL                                                  | API using graphql-kotlin                   |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
| -------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

## GraphQL API

The primary way to interact with this e-commerce backend is through its GraphQL API.

- **Main Endpoint:** `/graphql`
- **GraphiQL Console:** `/graphiql` (for interactive queries and schema exploration)

Here's an example query to fetch all products' names and prices:

```graphql
query {
  products {
    name
    price
  }
}
```

## Configuration

Currently, the application uses default configurations for port and hardcoded CORS settings. For production environments, consider making these configurable via environment variables or a configuration file. Key areas for configuration would be the server port and allowed CORS origins.
