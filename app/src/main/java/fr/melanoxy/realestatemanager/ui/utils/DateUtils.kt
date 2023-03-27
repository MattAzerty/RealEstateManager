package fr.melanoxy.realestatemanager.ui.utils

import java.text.SimpleDateFormat
import java.util.*

fun toDateFormat(date: String): Date {
    val pattern = "dd/MM/yyyy"
    val formatter = SimpleDateFormat(pattern)
    return formatter.parse(date) ?: Date()
}