# GraphQL Schema Documentation

This document provides a detailed overview of the GraphQL schema used in the application.

## Schema Definition

The schema defines the types, queries, and directives available for interacting with the e-commerce catalog.

```graphql
schema {
    query: Query
}
```

## Directives

*   `@deprecated(reason: String! = "No longer supported")`: Marks the field, argument, input field or enum value as deprecated.
*   `@experimental_disableErrorPropagation`: This directive disables error propagation when a non nullable field returns null for the given operation.
*   `@include(if: Boolean!)`: Directs the executor to include this field or fragment only when the `if` argument is true.
*   `@oneOf`: Indicates an Input Object is a OneOf Input Object.
*   `@skip(if: Boolean!)`: Directs the executor to skip this field or fragment when the `if` argument is true.
*   `@specifiedBy(url: String!)`: Exposes a URL that specifies the behaviour of this scalar.

## Types

### `Product`

Represents a product available in the e-commerce catalog.

| Field           | Type         | Description                                     |
| :-------------- | :----------- | :---------------------------------------------- |
| `id`            | `ID!`        | Unique identifier of the product                |
| `name`          | `String!`    | Name of the product                             |
| `price`         | `BigDecimal!`| Price of the product in USD                     |
| `description`     | `String!`    | Detailed description of the product             |
| `images`        | `[String!]!` | List of image URLs associated with the product  |
| `material`      | `String!`    | Material composition of the product             |
| `inStock`       | `Boolean!`   | Indicates whether the product is currently in stock |
| `countryOfOrigin` | `String!`    | Country where the product was manufactured      |

## Queries

### `Query`

The root query type for fetching data.

| Field     | Arguments | Return Type  | Description                                                              |
| :-------- | :-------- | :----------- | :----------------------------------------------------------------------- |
| `products`|           | `[Product!]!`| Retrieve all products available in the catalog                           |
| `product` | `id: ID!` | `Product`   | Fetch a single product by its unique identifier. Throws `ProductNotFoundException` if not found. |


## Scalars

### `BigDecimal`

An arbitrary precision signed decimal.