package com.example.mobile.presentation.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {

    fun formatDate(date: String?): String {

        if (date.isNullOrBlank()) {
            return "Fecha no disponible"
        }

        return try {

            val parsedDate = LocalDateTime.parse(date)

            val utcDateTime = parsedDate.atOffset(ZoneOffset.UTC)

            val localDateTime = utcDateTime
                .atZoneSameInstant(ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofPattern(
                "dd 'de' MMMM yyyy • hh:mm a",
                Locale("es", "ES")
            )

            localDateTime.format(formatter)

        } catch (e: Exception) {
            date
        }
    }
}