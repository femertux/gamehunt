package com.mobileni.gamehunt.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatReleaseDate(date: String): String {
    return try {
        val parsed = LocalDate.parse(date)
        val formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)
        parsed.format(formatter)
    } catch (e: Exception) {
        date
    }
}