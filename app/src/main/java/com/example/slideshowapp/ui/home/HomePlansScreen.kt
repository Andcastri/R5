package com.example.slideshowapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.slideshowapp.data.model.HomePlan

@Composable
fun HomePlansScreen(
    plans: List<HomePlan>,
    onPlanClick: (HomePlan) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                onSearchQueryChange(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Buscar planes...") },
            singleLine = true
        )

        // Lista de planes
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(plans) { plan ->
                HomePlanCard(
                    plan = plan,
                    onClick = { onPlanClick(plan) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePlanCard(
    plan: HomePlan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = plan.imageUrl,
                contentDescription = "Imagen del plan ${plan.planName}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = plan.planName,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Internet: ${plan.internetSpeed}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "TV: ${plan.television}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Decodificador: ${plan.decoder}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Telefonía: ${plan.localPhone}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Precio: $${plan.price}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Código de tarifa: ${plan.tariffCode}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Campaña: ${plan.campaign}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
} 