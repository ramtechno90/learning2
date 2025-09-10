package com.example.menuapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.menuapp.viewmodels.RestaurantSelectionViewModel

/**
 * The first screen of the application where the user selects a restaurant by its ID.
 *
 * @param onNavigateToMenu A callback function to trigger navigation to the menu screen,
 *                         passing the selected restaurant ID.
 * @param vm The ViewModel for this screen, defaults to a new instance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantSelectionScreen(
    onNavigateToMenu: (Long) -> Unit,
    vm: RestaurantSelectionViewModel = viewModel()
) {
    val restaurantId by vm.restaurantId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select Restaurant") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = restaurantId,
                onValueChange = { vm.onRestaurantIdChange(it) },
                label = { Text("Enter Restaurant ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val id = restaurantId.toLongOrNull()
                    if (id != null) {
                        onNavigateToMenu(id)
                    }
                },
                enabled = restaurantId.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Proceed")
            }
        }
    }
}
