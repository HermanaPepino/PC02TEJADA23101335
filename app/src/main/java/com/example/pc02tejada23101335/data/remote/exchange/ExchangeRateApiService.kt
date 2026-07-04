package com.example.pc02tejada23101335.data.remote.exchange

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Define las rutas que consumiremos de ExchangeRate API.
 *
 * Usamos el endpoint:
 * /v6/{apiKey}/pair/{from}/{to}
 *
 * Ejemplo real:
 * /v6/TU_API_KEY/pair/USD/PEN
 */
interface ExchangeRateApiService {

    @GET("v6/{apiKey}/pair/{from}/{to}")
    suspend fun getPairRate(
        @Path("apiKey") apiKey: String,
        @Path("from") from: String,
        @Path("to") to: String
    ): ExchangePairResponse
}