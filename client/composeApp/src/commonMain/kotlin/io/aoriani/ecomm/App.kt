package io.aoriani.ecomm

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import io.aoriani.ecomm.di.appModule
import io.aoriani.ecomm.ui.navigation.Navigation
import org.koin.compose.KoinMultiplatformApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.logger.Level
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(
    onNavHostReady: suspend (NavController) -> Unit = {}
) {
    MaterialExpressiveTheme {
        KoinMultiplatformApplication(
            config = koinConfiguration {
                modules(appModule)
            },
            logLevel = Level.DEBUG
        ) {
            Navigation(onNavHostReady = onNavHostReady)
        }
    }
}