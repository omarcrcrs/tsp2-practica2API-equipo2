package com.example.p2_api.data

data class Ingredient(val name: String, val measure: String)

/** Modelo limpio para la UI: ya sin los 40 campos sueltos strIngredientN/strMeasureN. */
data class Meal(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val thumbnailUrl: String,
    val youtubeUrl: String?,
    val ingredients: List<Ingredient>
)

/** Convierte el DTO "crudo" de la API en el modelo Meal que consume la UI. */
fun MealDto.toMeal(): Meal {
    val ingredientFields = listOf(
        strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
        strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
        strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
        strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
    )
    val measureFields = listOf(
        strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
        strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
        strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
        strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
    )
    val ingredients = ingredientFields.indices.mapNotNull { i ->
        val name = ingredientFields[i]?.trim()
        if (name.isNullOrBlank()) null
        else Ingredient(name = name, measure = measureFields[i]?.trim().orEmpty())
    }
    return Meal(
        id = idMeal.orEmpty(),
        name = strMeal.orEmpty(),
        category = strCategory.orEmpty(),
        area = strArea.orEmpty(),
        instructions = strInstructions.orEmpty(),
        thumbnailUrl = strMealThumb.orEmpty(),
        youtubeUrl = strYoutube?.trim()?.takeIf { it.isNotBlank() },
        ingredients = ingredients
    )
}
