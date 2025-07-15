package io.aoriani.ecomm.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi

expect abstract class UiTest()

@OptIn(ExperimentalTestApi::class)
expect fun ComposeUiTest.setContentWithContext(composable: @Composable () -> Unit)