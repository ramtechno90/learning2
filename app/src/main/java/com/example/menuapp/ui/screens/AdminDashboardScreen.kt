package com.example.menuapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.menuapp.data.models.Order
import com.example.menuapp.ui.theme.SwiggyOrange
import com.example.menuapp.viewmodels.AdminDashboardViewModel
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToManagement: () -> Unit,
    onSignOut: () -> Unit,
    vm: AdminDashboardViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Pending", "Accepted", "Completed", "Rejected")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToManagement) { Icon(Icons.Default.Settings, contentDescription = "Manage") }
                    IconButton(onClick = { vm.signOut(); onSignOut() }) { Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = SwiggyOrange
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) },
                        selectedContentColor = SwiggyOrange,
                        unselectedContentColor = Color.Gray
                    )
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.error != null) {
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                } else {
                    val filteredOrders = uiState.allOrders.filter { it.status == tabs[selectedTabIndex] }
                    AdminOrderList(orders = filteredOrders, onUpdateStatus = vm::updateOrderStatus)
                }
            }
        }
    }
}

@Composable
fun AdminOrderList(orders: List<Order>, onUpdateStatus: (orderId: Long, newStatus: String) -> Unit) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("No orders in this category.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(orders, key = { it.id }) { order ->
                AdminOrderCard(order = order, onUpdateStatus = onUpdateStatus)
            }
        }
    }
}

@Composable
fun AdminOrderCard(order: Order, onUpdateStatus: (orderId: Long, newStatus: String) -> Unit) {
    Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order #${order.dailyOrderNumber}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$${"%.2f".format(order.total)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text("Customer: ${order.customerName}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            order.items.forEach { item ->
                val details = mutableListOf<String>()
                if (item.dineInQuantity > 0) details.add("${item.dineInQuantity}x Dine-in")
                if (item.takeawayQuantity > 0) details.add("${item.takeawayQuantity}x Takeaway")
                Text("${item.menuItemName} (${details.joinToString()})", style = MaterialTheme.typography.bodyMedium)
                item.specialInstructions?.let { Text("  ↳ Notes: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
            }
            if(order.status == "Pending" || order.status == "Accepted") {
                Spacer(modifier = Modifier.height(8.dp))
                AdminOrderCardActions(order = order, onUpdateStatus = onUpdateStatus)
            }
        }
    }
}

@Composable
fun AdminOrderCardActions(order: Order, onUpdateStatus: (orderId: Long, newStatus: String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        when (order.status) {
            "Pending" -> {
                Button(onClick = { onUpdateStatus(order.id, "Accepted") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("Accept") }
                Button(onClick = { onUpdateStatus(order.id, "Rejected") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Reject") }
            }
            "Accepted" -> {
                Button(onClick = { onUpdateStatus(order.id, "Completed") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SwiggyOrange)) { Text("Mark as Completed") }
            }
            else -> {}
        }
    }
}
