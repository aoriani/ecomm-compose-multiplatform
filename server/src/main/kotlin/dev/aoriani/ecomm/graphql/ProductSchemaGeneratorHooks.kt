package dev.aoriani.ecomm.graphql

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLType
import java.math.BigDecimal
import kotlin.reflect.KClass
import kotlin.reflect.KType

object ProductSchemaGeneratorHooks : SchemaGeneratorHooks {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
        BigDecimal::class, Number::class -> ExtendedScalars.GraphQLBigDecimal
        else -> null
    }
}