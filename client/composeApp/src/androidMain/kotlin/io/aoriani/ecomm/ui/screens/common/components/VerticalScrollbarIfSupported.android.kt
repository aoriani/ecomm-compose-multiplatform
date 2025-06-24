package io.aoriani.ecomm.ui.screens.common.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable

@Composable
actual fun BoxScope.VerticalScrollBarIfSupported(scrollState: ScrollState) = Unit

@Composable
actual fun BoxScope.VerticalScrollBarIfSupported(scrollState: LazyGridState) = Unit