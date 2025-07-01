package io.aoriani.ecomm.di

import io.aoriani.ecomm.ui.App
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun initKoin() {
    startKoin {
        modules(commonModule)
    }
}
