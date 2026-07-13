package dev.aoriani.ecomm.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DatabaseSettings(
    val url: String,
    val driver: String,
)

@Serializable
internal data class ImagesSettings(
    @SerialName("base-url")
    val baseUrl: String,
)

@Serializable
internal data class McpSettings(
    @SerialName("server-name")
    val serverName: String,
    @SerialName("server-version")
    val serverVersion: String,
    @SerialName("allowed-hosts")
    val allowedHosts: List<String>,
    @SerialName("allowed-origins")
    val allowedOrigins: List<String>,
)
