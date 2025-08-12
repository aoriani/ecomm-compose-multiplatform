# Model Context Protocol (MCP) Server Documentation

This document provides an overview of the Model Context Protocol (MCP) server implementation for the eCommerce application. The MCP server exposes a set of tools that allow a model (like Gemini) to interact with the application's data in a structured way.

## 🚀 Overview

The MCP server is integrated into the main Ktor backend application. It leverages the `io.modelcontextprotocol.kotlin.sdk` to define and handle tool calls. The primary purpose of this server is to provide a programmatic interface for AI models to query product information from the e-commerce database.

## 🏛️ Architecture

The MCP server acts as a bridge between the AI model and the application's business logic (specifically, the `ProductRepository`). When the model needs to access data, it makes a "tool call" to the MCP server, which then executes the corresponding logic and returns the result.

```mermaid
graph TD
    subgraph "AI Model"
        A[Gemini]
    end

    subgraph "eCommerce Backend (Ktor)"
        B[MCP Server]
        C1[GetAllProductsTool]
        C2[GetProductByIdTool]
        D1[GetAllProductsUseCase]
        D2[GetProductByIdUseCase]
        E[ProductRepository]
        F[Database]
    end

    A -- "callTool(...)" --> B
    B -- "Executes tool handler" --> C1
    B -- "Executes tool handler" --> C2
    C1 -- "Invokes" --> D1
    C2 -- "Invokes" --> D2
    D1 -- "Fetches data" --> E
    D2 -- "Fetches data" --> E
    E -- "Queries" --> F
    F -- "Returns data" --> E
    E -- "Returns data" --> D1
    E -- "Returns data" --> D2
    D1 -- "Returns Result" --> C1
    D2 -- "Returns Result" --> C2
    C1 -- "Returns CallToolResult" --> B
    C2 -- "Returns CallToolResult" --> B
    B -- "Sends result" --> A
```

### Initialization

The MCP server is configured and initialized within the Ktor application lifecycle using an extension function `Application.configureMcp()`. This function sets up the server, defines its capabilities, and registers the available tools directly.

*Source: `server/src/main/kotlin/dev/aoriani/ecomm/config/mcp.kt`*

```kotlin
internal fun Application.configureMcp() {
    val serverName = environment.config.property("ecomm.mcp.server-name").getString()
    val serverVersion = environment.config.property("ecomm.mcp.server-version").getString()
    mcp {
        Server(
            serverInfo = Implementation(serverName, serverVersion),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = null)
                )
            )
        ).apply {
            val getAllProductsUseCase: GetAllProductsUseCase by dependencies
            val getProductByIdUseCase: GetProductByIdUseCase by dependencies
            addTool(GetAllProductsTool(getAllProductsUseCase))
            addTool(GetProductByIdTool(getProductByIdUseCase))
        }
    }
}
```

## 🛠️ Available Tools

The MCP tools are implemented as individual classes that implement the `McpTool` interface. Each tool is responsible for handling a specific type of request for product information.

*Source: `server/src/main/kotlin/dev/aoriani/ecomm/presentation/mcp/tools/`*

### Product Schema

Both tools return data that conforms to the following `Product` schema. This schema defines the structure of a single product object.

| Field             | Type      | Description                               |
| ----------------- | --------- | ----------------------------------------- |
| `id`              | `string`  | Unique identifier for the product.        |
| `name`            | `string`  | Name of the product.                      |
| `price`           | `number`  | Unit price of the product.                |
| `description`     | `string`  | Detailed text description of the product. |
| `images`          | `array`   | Array of image URLs for the product.      |
| `material`        | `string`  | Material the product is made of.          |
| `inStock`         | `boolean` | Indicates if the product is in stock.     |
| `countryOfOrigin` | `string`  | Country where the product is made.        |

---

### 1. `get_products_list`

This tool retrieves a complete list of all products available in the catalog.

-   **Description**: Retrieves all products in the catalog. Takes no input parameters and returns an object with a 'products' array.
-   **Input Schema**: None.
-   **Output Schema**: An object containing a `products` array. Each item in the array is a product object that conforms to the [Product Schema](#product-schema).

#### Interaction Flow

```mermaid
sequenceDiagram
    participant M as Model
    participant S as MCP Server
    participant T as GetAllProductsTool
    participant U as GetAllProductsUseCase
    participant R as ProductRepository

    M->>S: callTool("get_products_list", {})
    S->>T: execute(request)
    T->>U: invoke()
    U->>R: getAll()
    R-->>U: List<Product>
    U-->>T: Result<List<Product>>
    T-->>S: CallToolResult(structuredContent={ "products": [...] })
    S-->>M: Tool Result
```

---

### 2. `get_product_by_id`

This tool retrieves the details of a single product, identified by its unique ID.

-   **Description**: Retrieves details of a single product identified by a unique product ID. It takes the product ID as input and returns the corresponding product object.
-   **Input Schema**: An object with a required `id` property (string), conforming to the `GetProductByIdRequest` class.
-   **Output Schema**: A product object that conforms to the [Product Schema](#product-schema).

#### Interaction Flow

```mermaid
sequenceDiagram
    participant M as Model
    participant S as MCP Server
    participant T as GetProductByIdTool
    participant U as GetProductByIdUseCase
    participant R as ProductRepository

    M->>S: callTool("get_product_by_id", { "id": "some-product-id" })
    S->>T: execute(request)
    T->>U: invoke("some-product-id")
    U->>R: getById("some-product-id")
    R-->>U: Product?
    U-->>T: Result<Product>
    alt Product Found
        T-->>S: CallToolResult(structuredContent={...product...})
    else Product Not Found
        T-->>S: CallToolResult(isError=true, content="Unknown error")
    end
    S-->>M: Tool Result
```