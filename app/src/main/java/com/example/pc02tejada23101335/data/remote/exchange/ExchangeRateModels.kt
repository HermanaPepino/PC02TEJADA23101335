package com.example.pc02tejada23101335.data.remote.exchange

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de ExchangeRate API para el endpoint:
 * GET https://v6.exchangerate-api.com/v6/YOUR-API-KEY/pair/EUR/GBP
 *
 * La API devuelve nombres con guion bajo, por eso usamos @SerializedName.
 */
data class ExchangePairResponse(
    @SerializedName("result")
    val result: String? = null,

    @SerializedName("base_code")
    val baseCode: String? = null,

    @SerializedName("target_code")
    val targetCode: String? = null,

    @SerializedName("conversion_rate")
    val conversionRate: Double? = null,

    @SerializedName("error-type")
    val errorType: String? = null
)