package dev.aoriani.ecomm.config

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.slf4j.LoggerFactory
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CallLoggingConfigTest {

    private fun attachListAppender(): ListAppender<ILoggingEvent> {
        val logger = LoggerFactory.getLogger("Application") as Logger
        val listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()
        logger.addAppender(listAppender)
        return listAppender
    }

    @Ignore
    @Test
    fun `call logging writes formatted request log line`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ecomm.logging.level" to "INFO",
            )
        }
        val appender = attachListAppender()

        application {
            configureCallLogging()
            routing {
                get("/log-test") { call.respondText("ok", ContentType.Text.Plain) }
            }
        }

        val response = client.get("/log-test")
        assertEquals(HttpStatusCode.OK, response.status)
        response.bodyAsText() // ensure body consumed

        val messages = appender.list.map { it.formattedMessage }
        assertTrue(messages.any { it.startsWith("Request: GET /log-test") && it.contains("Status: 200") })
    }
}
