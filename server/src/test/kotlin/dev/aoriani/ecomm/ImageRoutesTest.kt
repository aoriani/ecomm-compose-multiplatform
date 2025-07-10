package dev.aoriani.ecomm

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

class ImageRoutesTest {

    private val testImageName = "test_image.png"
    private val nonExistentImageName = "non_existent_image.png"
    private val notAnImageName = "not_an_image.txt"
    private val testResourcesPath = "src/test/resources/static/images" // Relative to server module root

    @BeforeTest
    fun setup() {
        // Create dummy files for testing
        val imageDir = File("static/images") // Ktor's testApplication resolves relative to content root
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }

        // Create a simple 100x100 PNG for testing
        val testImageFile = File(imageDir, testImageName)
        if (!testImageFile.exists()) {
            val image = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
            val graphics = image.createGraphics()
            graphics.color = java.awt.Color.RED
            graphics.fillRect(0, 0, 100, 100)
            graphics.dispose()
            ImageIO.write(image, "png", testImageFile)
        }

        // Create a dummy non-image file
        val notAnImageFile = File(imageDir, notAnImageName)
        if (!notAnImageFile.exists()) {
            notAnImageFile.writeText("This is not an image.")
        }
    }

    @AfterTest
    fun tearDown() {
        // Clean up dummy files
        File("static/images/$testImageName").delete()
        File("static/images/$notAnImageName").delete()
        File("static/images").delete() // remove dir if empty
        File("static").delete() // remove dir if empty
    }

    private fun setupTestApplication(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        environment {
            // Configure the content root to point to where Ktor's staticResources will look for files.
            // For testing, Ktor's testApplication has a temporary content root.
            // We need to ensure our ImageRoutes.kt (which uses File("server/src/main/resources/..."))
            // can find the files.
            // The ImageRoutes.kt uses a hardcoded path to "server/src/main/resources/static/images"
            // For tests, we should ideally mock this or use a configurable path.
            // Given the current implementation of ImageRoutes, we'll copy test files to that location
            // or adjust ImageRoutes to be more testable.

            // For simplicity in this step, the @BeforeTest setup creates files in "static/images"
            // relative to the execution directory of the test, which Ktor's testApplication should pick up
            // if `staticResources` is configured with a relative path.
            // However, our `ImageRoutes.kt` specifically uses `File("server/src/main/resources/static/images/$imageName")`
            // This means we need to ensure that path is valid *or* modify ImageRoutes.kt to be testable.

            // Let's adjust the test to copy files to the expected "main" location for ImageRoutes to find them.
            // This is a bit of a workaround due to the hardcoded path in ImageRoutes.
            val mainStaticDir = File("server/src/main/resources/static/images")
            if (!mainStaticDir.exists()) mainStaticDir.mkdirs()

            val testImageSource = File("static/images/$testImageName") // From @BeforeTest setup
            if (testImageSource.exists()) testImageSource.copyTo(File(mainStaticDir, testImageName), overwrite = true)

            val notAnImageSource = File("static/images/$notAnImageName") // From @BeforeTest setup
            if (notAnImageSource.exists()) notAnImageSource.copyTo(File(mainStaticDir, notAnImageName), overwrite = true)
        }
        application {
            module() // Loads the main application module, including routing
        }
        block(this)

        // Cleanup after test execution block
        File("server/src/main/resources/static/images/$testImageName").delete()
        File("server/src/main/resources/static/images/$notAnImageName").delete()
        // Potentially remove directory if empty, but be careful not to delete actual main resources
    }


    @Test
    fun `test get resized image success`() = setupTestApplication {
        val response = client.get("/images/resized/$testImageName?width=50")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Image.PNG.toString(), response.headers[HttpHeaders.ContentType])

        val imageBytes = response.bodyBytes()
        val bufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))
        assertNotNull(bufferedImage, "Response should be a valid image")
        assertEquals(50, bufferedImage.width, "Image width should be 50")
        // Original is 100x100, so aspect ratio is 1.0. Resized should also be 1.0.
        assertEquals(50, bufferedImage.height, "Image height should maintain aspect ratio")
    }

    @Test
    fun `test get original image when no width provided`() = setupTestApplication {
        val response = client.get("/images/resized/$testImageName")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Image.PNG.toString(), response.headers[HttpHeaders.ContentType])

        val imageBytes = response.bodyBytes()
        val bufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))
        assertNotNull(bufferedImage)
        assertEquals(100, bufferedImage.width, "Should serve original width")
        assertEquals(100, bufferedImage.height, "Should serve original height")
    }

    @Test
    fun `test get image invalid width parameter`() = setupTestApplication {
        val response = client.get("/images/resized/$testImageName?width=abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid width parameter", response.bodyAsText())

        val responseNegative = client.get("/images/resized/$testImageName?width=-50")
        assertEquals(HttpStatusCode.BadRequest, responseNegative.status)
        assertEquals("Invalid width parameter", responseNegative.bodyAsText())

        val responseZero = client.get("/images/resized/$testImageName?width=0")
        assertEquals(HttpStatusCode.BadRequest, responseZero.status)
        assertEquals("Invalid width parameter", responseZero.bodyAsText())
    }

    @Test
    fun `test get image width too small resulting in zero height`() = setupTestApplication {
        // Create a very wide, short image for this test (e.g., 1000w x 1h)
        // For simplicity, we'll use the existing 100x100 and test with a very small width
        // that might cause issues if height calculation truncates to 0.
        // The current code calculates targetHeight = (targetWidth * aspectRatio).toInt()
        // If original is 100x100, aspectRatio = 1. targetHeight = targetWidth.
        // If original is 1x100, aspectRatio = 100. targetHeight = targetWidth * 100.
        // If original is 100x1, aspectRatio = 0.01. targetHeight = targetWidth * 0.01.
        // If targetWidth is 50, targetHeight = 0. This is handled.

        // Let's use a case where aspect ratio < 1 and targetWidth is small
        // Create a 100x1 image for this specific test case
        val wideImageName = "wide_test_image.png"
        val wideImageFile = File("server/src/main/resources/static/images", wideImageName)
        val image = BufferedImage(100, 1, BufferedImage.TYPE_INT_RGB)
        ImageIO.write(image, "png", wideImageFile)

        val response = client.get("/images/resized/$wideImageName?width=50") // height = 50 * (1/100) = 0.5 -> 0
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Calculated height is invalid, try a larger width.", response.bodyAsText())

        wideImageFile.delete() // Clean up this specific test image
    }


    @Test
    fun `test get image not found`() = setupTestApplication {
        val response = client.get("/images/resized/$nonExistentImageName?width=50")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Image not found", response.bodyAsText())
    }

    @Test
    fun `test get image cannot read image file`() = setupTestApplication {
        // This test relies on notAnImageName being a file that ImageIO.read() returns null for.
        val response = client.get("/images/resized/$notAnImageName?width=50")
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("Could not read image", response.bodyAsText())
    }
}
