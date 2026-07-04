package com.example.pc02tejada23101335.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pc02tejada23101335.data.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel de la pantalla de conversión.
 *
 * Su trabajo es manejar la lógica y dejar que la pantalla solo se encargue
 * de dibujar la interfaz.
 */
class ConversionViewModel : ViewModel() {

    private val repository = CurrencyRepository()

    val currencies = repository.supportedCurrencies

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    init {
        // Al iniciar, guardamos las monedas base en Firestore.
        viewModelScope.launch {
            repository.seedCurrenciesIfNeeded()
        }
    }

    fun onAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(
            amountText = value,
            errorMessage = null,
            resultMessage = null
        )
    }

    fun onFromCurrencyChange(value: String) {
        _uiState.value = _uiState.value.copy(
            fromCurrency = value,
            errorMessage = null,
            resultMessage = null
        )
    }

    fun onToCurrencyChange(value: String) {
        _uiState.value = _uiState.value.copy(
            toCurrency = value,
            errorMessage = null,
            resultMessage = null
        )
    }

    fun swapCurrencies() {
        val current = _uiState.value

        _uiState.value = current.copy(
            fromCurrency = current.toCurrency,
            toCurrency = current.fromCurrency,
            errorMessage = null,
            resultMessage = null
        )
    }

    fun convert() {
        val current = _uiState.value

        val amount = current.amountText
            .replace(",", ".")
            .toDoubleOrNull()

        if (amount == null || amount <= 0.0) {
            _uiState.value = current.copy(
                errorMessage = "Ingresa un monto válido mayor que cero."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(
                isLoading = true,
                errorMessage = null,
                resultMessage = null
            )

            val result = repository.convertAndSave(
                amount = amount,
                fromCurrency = current.fromCurrency,
                toCurrency = current.toCurrency
            )

            result
                .onSuccess { conversion ->
                    val amountFormatted = "%.2f".format(Locale.US, conversion.amount)
                    val resultFormatted = "%.2f".format(Locale.US, conversion.result)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        resultMessage = "$amountFormatted ${conversion.fromCurrency} equivalen a $resultFormatted ${conversion.toCurrency}"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al convertir."
                    )
                }
        }
    }
}