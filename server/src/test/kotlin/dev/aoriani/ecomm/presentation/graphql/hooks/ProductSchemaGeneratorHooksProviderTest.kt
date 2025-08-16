package dev.aoriani.ecomm.presentation.graphql.hooks

import kotlin.test.Test
import kotlin.test.assertSame

class ProductSchemaGeneratorHooksProviderTest {

    @Test
    fun `When hooks is called then it returns ProductSchemaGeneratorHooks`() {
        // Given
        val provider = ProductSchemaGeneratorHooksProvider()
        
        // When
        val hooks = provider.hooks()
        
        // Then
        assertSame(ProductSchemaGeneratorHooks, hooks)
    }
}