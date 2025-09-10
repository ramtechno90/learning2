package com.example.menuapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.menuapp.data.models.Category
import com.example.menuapp.data.models.MenuItem
import com.example.menuapp.ui.theme.SwiggyOrange
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
            TopAppBar(
                title = { Text(menuUiState.restaurant?.name ?: "Menu") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface)
            )
        },
        floatingActionButton = {
            if (cartUiState.totalItemCount > 0) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToCart,
                    containerColor = SwiggyOrange,
                    contentColor = Color.White,
                    text = { Text("View Cart (${cartUiState.totalItemCount})") },
                    icon = { }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (menuUiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (menuUiState.error != null) {
                Text(menuUiState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center).padding(16.dp))
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
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
        item {
            AsyncImage(
                model = restaurant.logoUrl,
                contentDescription = "${restaurant.name} Logo",
                modifier = Modifier.fillMaxWidth().height(220.dp),
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
                        expandedCategories = if (category.id in expandedCategories) expandedCategories - category.id
                        else expandedCategories + category.id
                    }
                )
                AnimatedVisibility(visible = category.id in expandedCategories) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        itemsInCategory.forEach { menuItem ->
                            MenuItemCard(item = menuItem, onAddToCart = { onAddToCart(menuItem) })
                            Divider(color = MaterialTheme.colorScheme.background)
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
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = category.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ArrowDropDown, "Expand", Modifier.rotate(if (isExpanded) 180f else 0f))
    }
    Divider()
}

@Composable
fun MenuItemCard(item: MenuItem, onAddToCart: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            item.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) }
            Text("$${"%.2f".format(item.price)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.size(width = 120.dp, height = 120.dp).clip(RoundedCornerShape(12.dp))) {
            Box(modifier = Modifier.fillMaxSize().align(Alignment.Center).clip(RoundedCornerShape(12.dp)))
            Button(
                onClick = onAddToCart,
                enabled = item.inStock,
                modifier = Modifier.align(Alignment.BottomCenter).offset(y = (20).dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (item.inStock) MaterialTheme.colorScheme.surface else Color.LightGray,
                    contentColor = if (item.inStock) SwiggyOrange else Color.Gray
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(if (item.inStock) "ADD" else "Unavailable", fontWeight = FontWeight.Bold)
            }
        }
    }
}
