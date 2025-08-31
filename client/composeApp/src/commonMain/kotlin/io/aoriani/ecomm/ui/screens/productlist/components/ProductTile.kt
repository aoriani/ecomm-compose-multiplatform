package io.aoriani.ecomm.ui.screens.productlist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.compose_multiplatform
import ecommerceapp.composeapp.generated.resources.product_list_add_to_cart_button_content_description
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.ui.screens.productlist.ProductListScreenTestTags
import io.aoriani.ecomm.ui.screens.productlist.productlist
import io.aoriani.ecomm.ui.test.TestTags
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductTile(
    product: ProductPreview,
    modifier: Modifier = Modifier,
    onTileClicked: () -> Unit = {},
    onAddToCartClicked: () -> Unit = {}
) {
    Card(modifier = modifier.padding(8.dp), onClick = onTileClicked) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProductPrice(price = product.price)
                AddToCartButton(onAddToCart = onAddToCartClicked)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductImage(product: ProductPreview) {
    val isPreview = LocalInspectionMode.current
    val contentScale = ContentScale.Crop
    val modifier = Modifier
        .size(100.dp)
        .clip(MaterialShapes.PixelCircle.toShape())

    if (!isPreview) {
        AsyncImage(
            model = product.thumbnailUrl,
            contentScale = contentScale,
            contentDescription = product.name,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(Res.drawable.compose_multiplatform),
            contentScale = contentScale,
            contentDescription = product.name,
            modifier = modifier
        )
    }
}

@Composable
fun ProductPrice(price: DollarAmount) {
    Text(
        text = "$$price", // Add currency symbol
        style = MaterialTheme.typography.bodyMedium // Smaller price
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddToCartButton(onAddToCart: () -> Unit = {}) {
    val size = ButtonDefaults.ExtraSmallContainerHeight
    IconButton(
        onClick = onAddToCart,
        modifier = Modifier.heightIn(size).testTag(TestTags.screens.productlist.addTocartButton),
        shapes = IconButtonDefaults.shapes(),
        colors = IconButtonDefaults.filledIconButtonColors()
    ) {
        Icon(
            Icons.Default.AddShoppingCart,
            contentDescription = stringResource(Res.string.product_list_add_to_cart_button_content_description),
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
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
