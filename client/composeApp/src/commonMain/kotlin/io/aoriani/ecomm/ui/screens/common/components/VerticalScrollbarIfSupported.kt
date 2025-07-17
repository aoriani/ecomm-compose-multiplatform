package io.aoriani.ecomm.ui.screens.common.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BoxScope.VerticalScrollbarIfSupported(scrollState: ScrollState, modifier: Modifier = Modifier) {
    PlatformVerticalScrollbar(scrollState, modifier.align(Alignment.CenterEnd).fillMaxHeight())
}

@Composable
fun BoxScope.VerticalScrollbarIfSupported(
    scrollState: LazyGridState,
    modifier: Modifier = Modifier
) {
    PlatformVerticalScrollbar(scrollState, modifier.align(Alignment.CenterEnd).fillMaxHeight())
}

@Composable
expect fun PlatformVerticalScrollbar(scrollState: ScrollState, modifier: Modifier = Modifier)

@Composable
expect fun PlatformVerticalScrollbar(scrollState: LazyGridState, modifier: Modifier = Modifier)