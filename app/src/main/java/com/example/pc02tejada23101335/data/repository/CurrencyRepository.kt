package com.example.pc02tejada23101335.data.repository

import com.example.pc02tejada23101335.data.model.ConversionRecord
import com.example.pc02tejada23101335.data.model.ConversionResult
import com.example.pc02tejada23101335.data.remote.exchange.ExchangeRateRetrofit
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Repositorio principal.
 *
 * Aquí se centraliza:
 * - Consulta a ExchangeRate API.
 * - Guardado de monedas/tasas en Firestore colección "rates".
 * - Guardado de conversiones en Firestore colección "conversions".
 * - Lectura del historial del usuario autenticado.
 */
class CurrencyRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val ratesCollection = firestore.collection("rates")
    private val conversionsCollection = firestore.collection("conversions")

    /**
     * Monedas mínimas solicitadas por el PDF.
     */
    val supportedCurrencies = listOf("USD", "EUR", "PEN", "GBP", "JPY")

    /**
     * Guarda las monedas disponibles en la colección "rates".
     *
     * Esto cumple la parte del PDF que pide una colección libre, ejemplo "rates",
     * donde se guarden las monedas.
     */
    suspend fun seedCurrenciesIfNeeded() {
        val currencies = listOf(
            mapOf(
                "code" to "USD",
                "name" to "Dólar estadounidense",
                "kind" to "currency",
                "active" to true
            ),
            mapOf(
                "code" to "EUR",
                "name" to "Euro",
                "kind" to "currency",
                "active" to true
            ),
            mapOf(
                "code" to "PEN",
                "name" to "Sol peruano",
                "kind" to "currency",
                "active" to true
            ),
            mapOf(
                "code" to "GBP",
                "name" to "Libra esterlina",
                "kind" to "currency",
                "active" to true
            ),
            mapOf(
                "code" to "JPY",
                "name" to "Yen japonés",
                "kind" to "currency",
                "active" to true
            )
        )

        val batch = firestore.batch()

        currencies.forEach { currency ->
            val code = currency["code"].toString()
            val document = ratesCollection.document(code)

            batch.set(
                document,
                currency + mapOf("createdOrUpdatedAt" to FieldValue.serverTimestamp()),
                SetOptions.merge()
            )
        }

        batch.commit().await()
    }

    /**
     * Convierte un monto usando la API y luego guarda el resultado en Firestore.
     */
    suspend fun convertAndSave(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Result<ConversionResult> {
        return try {
            val user = auth.currentUser
                ?: throw IllegalStateException("No hay usuario autenticado.")

            if (amount <= 0.0) {
                throw IllegalArgumentException("El monto debe ser mayor que cero.")
            }

            if (fromCurrency == toCurrency) {
                throw IllegalArgumentException("La moneda de origen y destino deben ser diferentes.")
            }

            // 1. Consultamos la tasa actual desde ExchangeRate API.
            val response = ExchangeRateRetrofit.api.getPairRate(
                apiKey = ExchangeRateRetrofit.API_KEY,
                from = fromCurrency,
                to = toCurrency
            )

            if (response.result != "success" || response.conversionRate == null) {
                throw IllegalStateException(
                    "No se pudo obtener la tasa. Error: ${response.errorType ?: "desconocido"}"
                )
            }

            val rate = response.conversionRate
            val convertedAmount = amount * rate

            // 2. Guardamos la tasa consultada en la colección "rates".
            saveRateInFirestore(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                rate = rate
            )

            val conversionResult = ConversionResult(
                amount = amount,
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                rate = rate,
                result = convertedAmount
            )

            // 3. Guardamos la conversión en la colección "conversions".
            val conversionData = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "dateTime" to FieldValue.serverTimestamp(),
                "amount" to amount,
                "fromCurrency" to fromCurrency,
                "toCurrency" to toCurrency,
                "conversionRate" to rate,
                "convertedAmount" to convertedAmount
            )

            conversionsCollection.add(conversionData).await()

            Result.success(conversionResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Guarda la última tasa consultada.
     *
     * Ejemplo de documento:
     * rates/USD_PEN
     */
    private suspend fun saveRateInFirestore(
        fromCurrency: String,
        toCurrency: String,
        rate: Double
    ) {
        val documentId = "${fromCurrency}_${toCurrency}"

        val rateData = mapOf(
            "kind" to "exchange_rate",
            "fromCurrency" to fromCurrency,
            "toCurrency" to toCurrency,
            "rate" to rate,
            "source" to "ExchangeRate API",
            "updatedAt" to FieldValue.serverTimestamp()
        )

        ratesCollection
            .document(documentId)
            .set(rateData, SetOptions.merge())
            .await()
    }

    /**
     * Escucha en tiempo real el historial del usuario actual.
     *
     * Usamos listener para que el historial se actualice automáticamente
     * cuando se guarde una nueva conversión.
     */
    fun listenUserHistory(
        onData: (List<ConversionRecord>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration? {
        val user = auth.currentUser

        if (user == null) {
            onError("No hay usuario autenticado.")
            return null
        }

        return conversionsCollection
            .whereEqualTo("uid", user.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.message ?: "Error leyendo historial.")
                    return@addSnapshotListener
                }

                val records = snapshot
                    ?.documents
                    ?.map { document -> document.toConversionRecord() }
                    ?.sortedByDescending { record -> record.dateText }
                    ?: emptyList()

                onData(records)
            }
    }

    /**
     * Convierte un documento de Firestore en ConversionRecord.
     */
    private fun DocumentSnapshot.toConversionRecord(): ConversionRecord {
        val timestamp = getTimestamp("dateTime")
        val dateText = timestamp.toReadableDate()

        return ConversionRecord(
            id = id,
            uid = getString("uid") ?: "",
            email = getString("email") ?: "",
            amount = getDouble("amount") ?: 0.0,
            fromCurrency = getString("fromCurrency") ?: "",
            toCurrency = getString("toCurrency") ?: "",
            conversionRate = getDouble("conversionRate") ?: 0.0,
            convertedAmount = getDouble("convertedAmount") ?: 0.0,
            dateText = dateText
        )
    }

    /**
     * Formatea la fecha/hora de Firestore.
     */
    private fun Timestamp?.toReadableDate(): String {
        if (this == null) return "Fecha pendiente"

        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formatter.format(this.toDate())
    }
}