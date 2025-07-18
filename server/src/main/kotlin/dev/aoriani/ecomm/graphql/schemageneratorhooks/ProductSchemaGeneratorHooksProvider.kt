package dev.aoriani.ecomm.graphql.schemageneratorhooks

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider

/**
 * Provides custom [SchemaGeneratorHooks] for the GraphQL schema generation process.
 *
 * This class is registered as a service provider to allow `graphql-kotlin` to discover
 * and use [ProductSchemaGeneratorHooks] during schema creation.
 */
class ProductSchemaGeneratorHooksProvider : SchemaGeneratorHooksProvider {
    override fun hooks(): SchemaGeneratorHooks = ProductSchemaGeneratorHooks
}