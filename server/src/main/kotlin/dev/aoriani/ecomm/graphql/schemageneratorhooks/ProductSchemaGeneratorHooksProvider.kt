package dev.aoriani.ecomm.graphql.schemageneratorhooks

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider

class ProductSchemaGeneratorHooksProvider : SchemaGeneratorHooksProvider {
    override fun hooks(): SchemaGeneratorHooks = ProductSchemaGeneratorHooks
}