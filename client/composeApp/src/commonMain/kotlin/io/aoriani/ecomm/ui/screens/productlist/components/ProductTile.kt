package io.aoriani.ecomm.ui.screens.productlist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.compose_multiplatform
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ProductPreview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductTile(product: ProductPreview, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(modifier = modifier.padding(8.dp), onClick = onClick) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProductImage(product = product)
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis

            )
            ProductPrice(price = product.price)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductImage(product: ProductPreview) {
    val isPreview = LocalInspectionMode.current
    if (!isPreview) {
        AsyncImage(
            model = product.thumbnailUrl,
            contentScale = ContentScale.Crop,
            contentDescription = product.name,
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialShapes.PixelCircle.toShape())
        )
    } else {
        Image(
            painter = painterResource(Res.drawable.compose_multiplatform),
            contentScale = ContentScale.Crop,
            contentDescription = product.name,
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialShapes.PixelCircle.toShape())
        )
    }
}

@Composable
fun ProductPrice(price: DollarAmount) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "$$price", // Add currency symbol
            style = MaterialTheme.typography.bodyMedium // Smaller price
        )
    }
}


@Preview(name = "Product Tile", showBackground = true)
@Composable
fun ProductTilePreview() {
    MaterialTheme {
        ProductTile(
            ProductPreview(
                id = ProductBasic.Id("id"),
                name = "Product Name",
                price = DollarAmount("10.0"),
                thumbnailUrl = "https://picsum.photos/200/300"
            )
        )
    }
}
