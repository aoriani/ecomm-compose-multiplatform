package io.aoriani.ecomm.ui.screens.common.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformVerticalScrollbar(scrollState: ScrollState, modifier: Modifier) = Unit

@Composable
actual fun PlatformVerticalScrollbar(scrollState: LazyGridState, modifier: Modifier) = Unit