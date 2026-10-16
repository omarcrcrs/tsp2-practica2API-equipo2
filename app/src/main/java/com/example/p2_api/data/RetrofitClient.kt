package com.example.p2_api.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Punto único de configuración de la API.
 *
 * La "1" al final de la URL es la API key de pruebas pública de TheMealDB
 * (documentada así por ellos mismos: se usa tal cual para desarrollo y
 * aprendizaje, sin necesidad de registrarte). Si algún día se publica la
 * app en una tienda, TheMealDB pide pasarse a una key de "supporter"; ese
 * día solo cambiaría esta constante.
 */
object RetrofitClient {

    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: MealApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MealApiService::class.java)
    }
}
