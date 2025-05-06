package io.aoriani.ecomm.ui.screens.productlist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.compose_multiplatform
import io.aoriani.ecomm.data.model.Product
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductTile(product: Product, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            val isPreview = LocalInspectionMode.current
            if (!isPreview) {
                AsyncImage(
                    model = product.images.first(),
                    contentScale = ContentScale.Crop,
                    contentDescription = product.name,
                    modifier = Modifier.size(100.dp)
                )
            } else {
                Image(
                    painterResource(Res.drawable.compose_multiplatform),
                    contentScale = ContentScale.Crop,
                    contentDescription = product.name,
                    modifier = Modifier.size(100.dp)
                )
            }

            Text(text = product.name)
            Text(
                text = product.price.toString(),
                modifier = modifier.align(Alignment.End),
            )
        }
    }
}


@Preview
@Composable
fun ProductTilePreview() {
    MaterialTheme {
        ProductTile(
            Product(
                id = "id",
                name = "Product Name",
                price = 10.0,
                description = "Product Description",
                images = persistentListOf("https://picsum.photos/200/300")

            )
        )
    }
}