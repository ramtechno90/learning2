package com.example.menuapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.menuapp.data.models.Category
import com.example.menuapp.data.models.MenuItem
import com.example.menuapp.viewmodels.CartViewModel
import com.example.menuapp.viewmodels.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel,
    onNavigateToCart: () -> Unit
) {
    val menuUiState by menuViewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(menuUiState.restaurant?.name ?: "Menu") })
        },
        floatingActionButton = {
            if (cartUiState.totalItemCount > 0) {
                FloatingActionButton(onClick = onNavigateToCart) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Cart: ${cartUiState.totalItemCount} items ($${"%.2f".format(cartUiState.total)})"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (menuUiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (menuUiState.error != null) {
                Text(
                    text = menuUiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                MenuContent(
                    restaurant = menuUiState.restaurant!!,
                    categories = menuUiState.categories,
                    menuItems = menuUiState.menuItems,
                    onAddToCart = { cartViewModel.addItem(it) }
                )
            }
        }
    }
}

@Composable
fun MenuContent(
    restaurant: com.example.menuapp.data.models.Restaurant,
    categories: List<Category>,
    menuItems: Map<Long, List<MenuItem>>,
    onAddToCart: (MenuItem) -> Unit
) {
    var expandedCategories by remember { mutableStateOf(setOf<Long>()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            AsyncImage(
                model = restaurant.logoUrl,
                contentDescription = "${restaurant.name} Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(categories, key = { it.id }) { category ->
            val itemsInCategory = menuItems[category.id] ?: emptyList()
            if (itemsInCategory.isNotEmpty()) {
                CategoryHeader(
                    category = category,
                    isExpanded = category.id in expandedCategories,
                    onClick = {
                        expandedCategories = if (category.id in expandedCategories) {
                            expandedCategories - category.id
                        } else {
                            expandedCategories + category.id
                        }
                    }
                )

                AnimatedVisibility(visible = category.id in expandedCategories) {
                    Column {
                        itemsInCategory.forEach { menuItem ->
                            MenuItemCard(item = menuItem, onAddToCart = { onAddToCart(menuItem) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryHeader(category: Category, isExpanded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Expand",
            modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
        )
    }
    Divider()
}

@Composable
fun MenuItemCard(item: MenuItem, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            item.description?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "$${"%.2f".format(item.price)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Button(onClick = onAddToCart, enabled = item.inStock) {
                    Text(if (item.inStock) "Add to Cart" else "Out of Stock")
                }
            }
        }
    }
}
