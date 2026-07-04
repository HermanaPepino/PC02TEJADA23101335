package com.example.pc02tejada23101335.presentation.converter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pc02tejada23101335.data.model.ConversionRecord
import com.example.pc02tejada23101335.data.repository.CurrencyRepository
import java.util.Locale

/**
 * Pantalla que muestra el historial de conversiones del usuario autenticado.
 */
@Composable
fun HistoryScreen() {
    val repository = remember { CurrencyRepository() }

    var records by remember { mutableStateOf<List<ConversionRecord>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val listener = repository.listenUserHistory(
            onData = { history ->
                records = history
                errorMessage = null
            },
            onError = { error ->
                errorMessage = error
            }
        )

        onDispose {
            listener?.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Historial de conversiones",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (records.isEmpty() && errorMessage == null) {
            Text("Aún no tienes conversiones registradas.")
        }

        LazyColumn {
            items(records) { record ->
                HistoryItem(record = record)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

/**
 * Tarjeta individual del historial.
 */
@Composable
private fun HistoryItem(record: ConversionRecord) {
    val amount = "%.2f".format(Locale.US, record.amount)
    val result = "%.2f".format(Locale.US, record.convertedAmount)
    val rate = "%.4f".format(Locale.US, record.conversionRate)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "$amount ${record.fromCurrency} → $result ${record.toCurrency}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tasa usada: $rate",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Fecha/Hora: ${record.dateText}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}