package io.aoriani.ecomm

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import io.aoriani.ecomm.di.appModule
import io.aoriani.ecomm.ui.navigation.Navigation
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinMultiplatformApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App(
    onNavHostReady: suspend (NavController) -> Unit = {}
) {
    MaterialTheme {
        KoinMultiplatformApplication(
            config = koinConfiguration {
                modules(appModule)
            }
        ) {
            Navigation(onNavHostReady)
        }
    }
}