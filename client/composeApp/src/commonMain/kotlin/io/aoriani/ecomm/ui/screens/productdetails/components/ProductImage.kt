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

/**
 * Displays a product image.
 *
 * This composable function displays an image from a given URL.
 * It uses [AsyncImage] for loading the image asynchronously.
 * In preview mode, it displays a placeholder image.
 *
 * @param imageUrl The URL of the image to display.
 * @param contentDescription The content description for the image, used for accessibility.
 * @param modifier The modifier to be applied to the image. Defaults to [Modifier].
 */
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
