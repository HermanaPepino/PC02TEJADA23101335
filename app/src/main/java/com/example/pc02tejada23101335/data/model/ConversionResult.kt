package com.example.pc02tejada23101335.data.model

/**
 * Resultado calculado luego de consultar la API.
 *
 * Este modelo se usa en pantalla para mostrar algo como:
 * "100 USD equivalen a 92.50 EUR".
 */
data class ConversionResult(
    val amount: Double,
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double,
    val result: Double
)