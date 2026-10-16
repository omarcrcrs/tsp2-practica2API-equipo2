@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.p2_api.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.p2_api.data.Ingredient
import com.example.p2_api.data.Meal

@Composable
fun MealScreen(viewModel: MealViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recetas · Práctica 2 (API)") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = { viewModel.searchMeal(query) }
            )

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.fetchRandomMeal() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎲 Receta aleatoria")
            }

            Spacer(Modifier.height(16.dp))

            // weight(1f) (no fillMaxSize) para que esta zona ocupe exactamente
            // el espacio restante debajo del buscador, sin empujar el layout
            // fuera de la pantalla.
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is MealUiState.Idle -> IdleContent()
                    is MealUiState.Loading -> LoadingContent()
                    is MealUiState.Error -> ErrorContent(message = state.message)
                    is MealUiState.Success -> MealContent(meal = state.meal)
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Buscar receta (ej. \"Arrabiata\")") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        Button(onClick = onSearch) {
            Text("Buscar")
        }
    }
}

@Composable
private fun IdleContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🍽️", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "Busca una receta por nombre o pide una aleatoria",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(8.dp))
        Text("Consultando la API...")
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚠️", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun MealContent(meal: Meal) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                AsyncImage(
                    model = meal.thumbnailUrl,
                    contentDescription = "Foto de ${meal.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(meal.name, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Row {
                        if (meal.category.isNotBlank()) {
                            AssistChip(onClick = {}, label = { Text(meal.category) })
                            Spacer(Modifier.width(8.dp))
                        }
                        if (meal.area.isNotBlank()) {
                            AssistChip(onClick = {}, label = { Text(meal.area) })
                        }
                    }
                }
            }
        }

        if (meal.ingredients.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Ingredientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
            }
            items(meal.ingredients) { ingredient ->
                IngredientRow(ingredient)
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Instrucciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(meal.instructions, style = MaterialTheme.typography.bodyMedium)
        }

        meal.youtubeUrl?.let { youtubeUrl ->
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl)))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("▶ Ver video de preparación")
                }
            }
        }
    }
}

@Composable
private fun IngredientRow(ingredient: Ingredient) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(ingredient.name, modifier = Modifier.weight(1f))
            Text(
                ingredient.measure,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
