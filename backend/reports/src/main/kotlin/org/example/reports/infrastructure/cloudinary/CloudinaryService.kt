package org.example.reports.infrastructure.cloudinary

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CloudinaryService(
    private val cloudinary: Cloudinary
) {

    fun uploadPhoto(file: MultipartFile): String {
        return try {
            val uploadResult = cloudinary.uploader().upload(
                file.bytes,
                ObjectUtils.asMap(
                    "folder", "reports_photos",
                    "resource_type", "image"
                )
            )
            uploadResult["secure_url"] as String
        } catch (e: Exception) {
            throw RuntimeException("Error uploading photo to Cloudinary: ${e.message}")
        }
    }
}