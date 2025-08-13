package dev.aoriani.ecomm.presentation.graphql.exceptions

class GraphQLInternalException(message: String = "An internal error occurred", cause: Throwable? = null) : RuntimeException(message, cause)
