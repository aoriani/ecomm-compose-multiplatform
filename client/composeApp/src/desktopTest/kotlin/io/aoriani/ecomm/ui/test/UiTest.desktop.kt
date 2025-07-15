package io.aoriani.ecomm.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi

actual abstract class UiTest

@OptIn(markerClass = [ExperimentalTestApi::class])
actual fun ComposeUiTest.setContentWithContext(composable: @Composable (() -> Unit))= setContent(composable)