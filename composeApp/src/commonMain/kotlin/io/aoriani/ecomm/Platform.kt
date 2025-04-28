package io.aoriani.ecomm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform