package io.aoriani.ecomm.ui.screens.productdetails.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.compose.AsyncImage
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductImage(imageUrl: String, contentDescription: String, modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    if (!isPreview) {
        AsyncImage(
            model = imageUrl,
            contentScale = ContentScale.FillWidth,
            contentDescription = contentDescription,
            modifier = modifier

        )
    } else {
        Image(
            painter = painterResource(Res.drawable.compose_multiplatform),
            contentScale = ContentScale.Fit,
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}
