package com.example.pc02tejada23101335.data.remote.exchange

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Crea una instancia única de Retrofit para llamar a ExchangeRate API.
 */
object ExchangeRateRetrofit {

    private const val BASE_URL = "https://v6.exchangerate-api.com/"

    /**
     * Pega aquí tu API KEY.
     *
     * Para una práctica está bien, pero en proyectos reales no conviene subir
     * API keys a GitHub público.
     */
    const val API_KEY = "3f9ba146b00190b2a43384ea"

    val api: ExchangeRateApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApiService::class.java)
    }
}