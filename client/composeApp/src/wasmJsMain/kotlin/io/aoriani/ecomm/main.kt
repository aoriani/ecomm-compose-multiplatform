package io.aoriani.ecomm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import io.aoriani.ecomm.di.commonModule
import io.aoriani.ecomm.ui.App
import io.aoriani.ecomm.ui.navigation.getBackStackEntryRoute
import kotlinx.browser.document
import kotlinx.browser.window
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalBrowserHistoryApi
fun main() {
    startKoin {
        modules(commonModule)
    }

    val body = document.body ?: error("Missing body tag")
    ComposeViewport(body) {
        App(onNavHostReady = {
            window.bindToNavigation(
                navController = it,
                getBackStackEntryRoute = ::getBackStackEntryRoute
            )
        })
    }
}