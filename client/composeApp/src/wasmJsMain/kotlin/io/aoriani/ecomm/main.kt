package io.aoriani.ecomm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.bindToNavigation
import io.aoriani.ecomm.ui.navigation.getBackStackEntryRoute
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalBrowserHistoryApi
fun main() {
    val body = document.body ?: error("Missing body tag")
    ComposeViewport(body) {
        App(onNavHostReady = { navController ->
            navController.bindToBrowserNavigation(::getBackStackEntryRoute)
        })
    }
}