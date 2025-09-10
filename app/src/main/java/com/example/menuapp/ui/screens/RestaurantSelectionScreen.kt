package com.example.menuapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.menuapp.ui.theme.SwiggyOrange
import com.example.menuapp.viewmodels.RestaurantSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantSelectionScreen(
    onNavigateToMenu: (Long) -> Unit,
    vm: RestaurantSelectionViewModel = viewModel()
) {
    val restaurantId by vm.restaurantId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Your Restaurant") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = restaurantId,
                onValueChange = { vm.onRestaurantIdChange(it) },
                label = { Text("Enter Restaurant ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { restaurantId.toLongOrNull()?.let { id -> onNavigateToMenu(id) } },
                enabled = restaurantId.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = SwiggyOrange)
            ) {
                Text("Find Menu", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
