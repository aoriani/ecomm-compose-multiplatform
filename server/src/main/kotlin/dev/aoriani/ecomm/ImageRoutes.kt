package dev.aoriani.ecomm

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import java.io.File

fun Route.imageRouting() {
    get("/images/resized/{imageName}") {
        val imageName = call.parameters["imageName"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing image name")
        val widthStr = call.request.queryParameters["width"]

        val imageFile = File("server/src/main/resources/static/images/$imageName")
        if (!imageFile.exists()) {
            return@get call.respond(HttpStatusCode.NotFound, "Image not found")
        }

        val originalImage = ImageIO.read(imageFile)
        if (originalImage == null) {
            return@get call.respond(HttpStatusCode.InternalServerError, "Could not read image")
        }

        if (widthStr == null) {
            // No width provided, serve original image
            call.respondFile(imageFile)
            return@get
        }

        val targetWidth = widthStr.toIntOrNull()
        if (targetWidth == null || targetWidth <= 0) {
            return@get call.respond(HttpStatusCode.BadRequest, "Invalid width parameter")
        }

        // Calculate target height to maintain aspect ratio
        val aspectRatio = originalImage.height.toDouble() / originalImage.width.toDouble()
        val targetHeight = (targetWidth * aspectRatio).toInt()

        if (targetHeight <= 0) {
             return@get call.respond(HttpStatusCode.BadRequest, "Calculated height is invalid, try a larger width.")
        }

        // Resize the image
        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null)
        graphics2D.dispose()

        // Convert BufferedImage to ByteArray
        val baos = ByteArrayOutputStream()
        val formatName = imageName.substringAfterLast('.', "png") // Default to png if no extension
        ImageIO.write(resizedImage, formatName, baos)
        val imageBytes = baos.toByteArray()

        // Determine content type based on file extension
        val contentType = when (formatName.lowercase()) {
            "jpg", "jpeg" -> ContentType.Image.JPEG
            "png" -> ContentType.Image.PNG
            "gif" -> ContentType.Image.GIF
            else -> ContentType.Image.PNG // Default to PNG
        }
        call.respondBytes(imageBytes, contentType)
    }
}
