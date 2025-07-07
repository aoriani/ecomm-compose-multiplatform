package io.aoriani.ecomm.di

import io.aoriani.ecomm.data.repositories.db.SqlDriverFactory
import org.koin.dsl.module

actual val sqlPlatformModule = module {
    single { SqlDriverFactory() }
}