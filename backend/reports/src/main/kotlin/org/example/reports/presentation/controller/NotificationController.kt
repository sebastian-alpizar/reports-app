package org.example.reports.presentation.controller

import org.example.reports.application.usecase.notifications.DeleteNotificationUseCase
import org.example.reports.application.usecase.notifications.GetNotificationsUseCase
import org.example.reports.domain.repository.UserRepository
import org.example.reports.presentation.dto.ApiResponse
import org.example.reports.presentation.dto.NotificationResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,
    private val userRepository: UserRepository
) {

    @GetMapping("/{userId}")
    fun getMyNotifications(@PathVariable userId: Long):
            ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        return try {
            val user = userRepository.findById(userId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        ApiResponse(
                            message = "Usuario no encontrado"
                        )
                    )

            if (!user.isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(
                        ApiResponse(
                            message = "Acceso denegado"
                        )
                    )
            }

            val notifications = getNotificationsUseCase.execute(userId)

            ResponseEntity.ok(
                ApiResponse(
                    message = "Notificaciones obtenidas exitosamente",
                    data = notifications
                )
            )

        } catch (e: Exception) {

            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ApiResponse(
                        message = "Error al obtener notificaciones: ${e.message}"
                    )
                )
        }
    }

    @DeleteMapping("/{notificationId}")
    fun deleteNotification(
        @PathVariable notificationId: String
    ): ResponseEntity<ApiResponse<String>> {

        return try {
            deleteNotificationUseCase.execute(notificationId)
            ResponseEntity.ok(
                ApiResponse(
                    message = "Notificación eliminada exitosamente",
                    data = notificationId
                )
            )

        } catch (e: Exception) {

            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ApiResponse(
                        message = "Error al eliminar notificación: ${e.message}"
                    )
                )
        }
    }
}