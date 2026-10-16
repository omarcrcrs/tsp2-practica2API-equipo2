package com.example.p2_api.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2_api.data.Meal
import com.example.p2_api.data.RetrofitClient
import com.example.p2_api.data.toMeal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * Representa TODO lo que la pantalla puede estar mostrando en un momento
 * dado. Usar una sealed interface (en vez de, por ejemplo, un booleano
 * "isLoading" + un Meal? + un String? error por separado) evita estados
 * imposibles: nunca vamos a poder tener "loading = true" Y "error" a la
 * vez por accidente, porque solo puede existir UNO de estos cuatro casos.
 */
sealed interface MealUiState {
    data object Idle : MealUiState
    data object Loading : MealUiState
    data class Success(val meal: Meal) : MealUiState
    data class Error(val message: String) : MealUiState
}

class MealViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MealUiState>(MealUiState.Idle)
    val uiState: StateFlow<MealUiState> = _uiState.asStateFlow()

    fun searchMeal(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) {
            _uiState.value = MealUiState.Error("Escribe el nombre de una receta para buscar")
            return
        }
        viewModelScope.launch {
            _uiState.value = MealUiState.Loading
            runCatching { RetrofitClient.api.searchMealByName(trimmedQuery) }
                .onSuccess { response ->
                    val meal = response.meals?.firstOrNull()
                    _uiState.value = if (meal != null) {
                        MealUiState.Success(meal.toMeal())
                    } else {
                        MealUiState.Error("No se encontró ninguna receta con \"$trimmedQuery\"")
                    }
                }
                .onFailure { error ->
                    _uiState.value = MealUiState.Error(error.toUserMessage())
                }
        }
    }

    fun fetchRandomMeal() {
        viewModelScope.launch {
            _uiState.value = MealUiState.Loading
            runCatching { RetrofitClient.api.getRandomMeal() }
                .onSuccess { response ->
                    val meal = response.meals?.firstOrNull()
                    _uiState.value = if (meal != null) {
                        MealUiState.Success(meal.toMeal())
                    } else {
                        MealUiState.Error("No se pudo obtener una receta aleatoria")
                    }
                }
                .onFailure { error ->
                    _uiState.value = MealUiState.Error(error.toUserMessage())
                }
        }
    }

    /** Traduce excepciones técnicas a un mensaje que el usuario sí entiende. */
    private fun Throwable.toUserMessage(): String = when (this) {
        is IOException -> "No hay conexión a internet. Revisa tu red e intenta de nuevo."
        is HttpException -> "El servidor respondió con un error (código ${code()})."
        else -> "Ocurrió un error inesperado: ${localizedMessage ?: "desconocido"}"
    }
}
