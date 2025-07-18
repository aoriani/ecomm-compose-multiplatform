package dev.aoriani.ecomm.graphql.schemageneratorhooks

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.generator.scalars.ID
import graphql.Scalars
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLType
import java.math.BigDecimal
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Custom hooks for `graphql-kotlin` schema generation.
 * These hooks allow customization of the GraphQL schema generation process.
 */
internal object ProductSchemaGeneratorHooks : SchemaGeneratorHooks {

    /**
     * Overrides the default GraphQL type generation for specific Kotlin classes.
     * Maps [BigDecimal] and [Number] to [ExtendedScalars.GraphQLBigDecimal] for precise decimal representation in GraphQL.
     *
     * @param type The Kotlin type being processed.
     * @return The [GraphQLType] to use for the given Kotlin type, or null to use default behavior.
     */
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
        BigDecimal::class, Number::class -> ExtendedScalars.GraphQLBigDecimal
        ID::class -> Scalars.GraphQLID
        else -> null
    }
}