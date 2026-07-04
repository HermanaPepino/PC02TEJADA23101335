package com.example.pc02tejada23101335.data.model

/**
 * Modelo que representa una conversión guardada en Firestore.
 *
 * Esta clase se usa para mostrar el historial en la app.
 */
data class ConversionRecord(
    val id: String = "",
    val uid: String = "",
    val email: String = "",
    val amount: Double = 0.0,
    val fromCurrency: String = "",
    val toCurrency: String = "",
    val conversionRate: Double = 0.0,
    val convertedAmount: Double = 0.0,
    val dateText: String = ""
)