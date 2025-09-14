package io.aoriani.ecomm.ui.screens.common.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Displays a vertical scrollbar if the current platform supports it.
 * This composable is designed to be used within a `BoxScope` to allow for alignment.
 *
 * @param scrollState The `ScrollState` that the scrollbar will interact with.
 * @param modifier The modifier to be applied to the scrollbar.
 *                 By default, it aligns the scrollbar to the `CenterEnd` of the `Box`
 *                 and makes it fill the maximum height.
 */
@Composable
fun BoxScope.VerticalScrollbarIfSupported(scrollState: ScrollState, modifier: Modifier = Modifier) {
    PlatformVerticalScrollbar(scrollState, modifier.align(Alignment.CenterEnd).fillMaxHeight())
}

/**
 * Displays a vertical scrollbar if the current platform supports it.
 * This overload is specifically for use with [LazyGridState].
 * The scrollbar will be aligned to the end of the [BoxScope] and fill its maximum height.
 *
 * @param scrollState The [LazyGridState] that this scrollbar will control.
 * @param modifier The [Modifier] to be applied to the scrollbar.
 */
@Composable
fun BoxScope.VerticalScrollbarIfSupported(
    scrollState: LazyGridState,
    modifier: Modifier = Modifier
) {
    PlatformVerticalScrollbar(scrollState, modifier.align(Alignment.CenterEnd).fillMaxHeight())
}

/**
 * Renders a platform-specific vertical scrollbar.
 * This is an `expect` function, meaning its actual implementation is provided by platform-specific modules (e.g., desktop, Android).
 *
 * @param scrollState The [ScrollState] that this scrollbar will interact with and represent.
 * @param modifier The [Modifier] to be applied to this scrollbar.
 */
@Composable
expect fun PlatformVerticalScrollbar(scrollState: ScrollState, modifier: Modifier = Modifier)

/**
 * Platform-specific implementation of a vertical scrollbar for a [LazyGridState].
 * This function is expected to be implemented in platform-specific source sets (e.g., desktopMain, androidMain).
 *
 * @param scrollState The [LazyGridState] to which this scrollbar will be attached.
 * @param modifier The modifier to be applied to the scrollbar.
 */
@Composable
expect fun PlatformVerticalScrollbar(scrollState: LazyGridState, modifier: Modifier = Modifier)