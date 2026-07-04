package com.example.pc02tejada23101335.presentation.converter

/**
 * Estado visual de la pantalla de conversión.
 *
 * Compose redibuja la pantalla cada vez que cambia este estado.
 */
data class ConverterUiState(
    val amountText: String = "",
    val fromCurrency: String = "USD",
    val toCurrency: String = "EUR",
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null
)