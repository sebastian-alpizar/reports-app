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
import tools.jackson.databind.ObjectMapper

@Service
class GeminiService(

    @Value("\${gemini.api-key}")
    private val apiKey: String
) {

    private val webClient = WebClient.builder()
        .baseUrl("https://generativelanguage.googleapis.com")
        .build()

    private val objectMapper = ObjectMapper()

    fun analyzeReport(
        photo: MultipartFile,
        description: String
    ): ReportAnalysisResponse {

        validateBasicRules(photo)

        val base64Image = Base64.getEncoder()
            .encodeToString(compressImage(photo))

        val prompt = """
            Analiza esta imagen y descripción para una aplicación de reportes ciudadanos.
    
            Descripción:
            $description
    
            Debes determinar:
    
            1. Si la imagen es válida
            2. La categoría
            3. La severidad del 1 al 5
    
            La imagen NO es válida si:
            - contiene desnudos
            - contiene violencia explícita
            - es selfie
            - es meme
            - es screenshot
            - no representa un problema urbano real
    
            Categorías posibles:
            - Basura
            - Hueco
            - Accidente
            - Inundación
            - Incendio
            - Cableado
            - Alumbrado
            - Vandalismo
            - Otro
    
            Responde únicamente con JSON válido.
            No utilices markdown.
            No utilices ```json.
            No agregues explicaciones.
    
            Ejemplo:
            {
              "valid": true,
              "category": "Hueco",
              "severity": 4
            }
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

            println("Respuesta cruda: $response")
            println("Respuesta parseada 1: $result")

            val json = result
                ?.replace("```json", "", ignoreCase = true)
                ?.replace("```JSON", "", ignoreCase = true)
                ?.replace("```", "")
                ?.trim()

            println("Respuesta parseada 2: $json")

            return objectMapper.readValue(
                result,
                ReportAnalysisResponse::class.java
            )

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