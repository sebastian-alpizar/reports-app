package org.example.reports.infrastructure.ai

import org.example.reports.infrastructure.ai.dto.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.client.WebClient
import java.util.Base64
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class GeminiService(

    @Value("\${gemini.api-key}")
    private val apiKey: String

) {

    private val webClient = WebClient.builder()
        .baseUrl("https://generativelanguage.googleapis.com")
        .build()

    fun validateImage(photo: MultipartFile): Boolean {

        validateBasicRules(photo)

        val base64Image = Base64.getEncoder()
            .encodeToString(compressImage(photo))

        val prompt = """
            Analiza esta imagen para una aplicación de reportes ciudadanos.

            La imagen:
            - debe representar un problema urbano real
            - no debe contener desnudos
            - no debe contener violencia explícita
            - no debe ser selfie
            - no debe ser meme
            - no debe ser screenshot
            - no debe ser contenido ofensivo

            Responde SOLO:
            VALID
            o
            INVALID
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(
                            text = prompt
                        ),
                        Part(
                            inlineData = InlineData(
                                mimeType = "image/jpeg",
                                data = base64Image
                            )
                        )
                    )
                )
            )
        )

        try {
            val response = webClient.post()
                //.uri("/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                //.uri("/v1beta/models/gemini-2.0-flash-exp:generateContent?key=$apiKey")
                //.uri("/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
                .uri("/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                .bodyValue(request)
                .retrieve()
                .onStatus({ it.isError }) { response ->
                    response.bodyToMono(String::class.java)
                        .map { body ->
                            RuntimeException("Gemini error: $body")
                        }
                }
                .bodyToMono(GeminiResponse::class.java)
                .block()

            val result = response
                ?.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?.trim()
                ?.uppercase()

            return result?.contains("VALID") == true &&
                    result.contains("INVALID").not()

        } catch (e: Exception) {
            println(e)
            throw RuntimeException(
                "El servicio de validación de imágenes no está disponible temporalmente"
            )
        }
    }

    private fun validateBasicRules(photo: MultipartFile) {

        if (photo.isEmpty) {
            throw RuntimeException("La imagen está vacía")
        }

        val contentType = photo.contentType ?: ""

        if (!contentType.startsWith("image/")) {
            throw RuntimeException("El archivo no es una imagen válida")
        }

        val maxSize = 10 * 1024 * 1024

        if (photo.size > maxSize) {
            throw RuntimeException("La imagen excede el tamaño permitido")
        }
    }

    private fun compressImage(photo: MultipartFile): ByteArray {

        val originalImage = ImageIO.read(photo.inputStream)

        val maxSize = 1024

        val originalWidth = originalImage.width
        val originalHeight = originalImage.height

        val scale = minOf(
            maxSize.toDouble() / originalWidth,
            maxSize.toDouble() / originalHeight,
            1.0
        )

        val newWidth = (originalWidth * scale).toInt()
        val newHeight = (originalHeight * scale).toInt()

        val resizedImage = BufferedImage(
            newWidth,
            newHeight,
            BufferedImage.TYPE_INT_RGB
        )

        val graphics = resizedImage.createGraphics()

        graphics.drawImage(
            originalImage,
            0,
            0,
            newWidth,
            newHeight,
            null
        )

        graphics.dispose()

        val outputStream = ByteArrayOutputStream()

        ImageIO.write(resizedImage, "jpg", outputStream)

        return outputStream.toByteArray()
    }
}