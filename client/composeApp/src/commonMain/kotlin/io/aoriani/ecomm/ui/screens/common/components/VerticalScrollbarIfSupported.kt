package io.aoriani.ecomm.ui.screens.common.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable

@Composable
expect fun BoxScope.VerticalScrollBarIfSupported(scrollState: ScrollState)

@Composable
expect fun BoxScope.VerticalScrollBarIfSupported(scrollState: LazyGridState)