package com.example.p2_api.data

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Definición de la API con Retrofit: cada función es un endpoint.
 * Al ser "suspend", Retrofit 3.x ya se encarga de correrlas fuera del hilo
 * principal y de lanzar la excepción directamente si algo falla (IOException
 * sin conexión, HttpException si el servidor responde con error).
 */
interface MealApiService {

    // GET https://www.themealdb.com/api/json/v1/1/search.php?s={name}
    @GET("search.php")
    suspend fun searchMealByName(@Query("s") name: String): MealResponseDto

    // GET https://www.themealdb.com/api/json/v1/1/random.php
    @GET("random.php")
    suspend fun getRandomMeal(): MealResponseDto
}
