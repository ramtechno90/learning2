package com.example.menuapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.menuapp.data.models.Order
import com.example.menuapp.viewmodels.AdminDashboardViewModel

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

    LaunchedEffect(Unit) {
        // Here you could listen for a specific event from ViewModel to trigger sign out
        // For now, it's handled by the sign out button.
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToManagement) {
                        Icon(androidx.compose.material.icons.filled.Settings, contentDescription = "Manage")
                    }
                    IconButton(onClick = {
                        vm.signOut()
                        onSignOut()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
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
                    OrderList(
                        orders = filteredOrders,
                        onUpdateStatus = { orderId, newStatus ->
                            vm.updateOrderStatus(orderId, newStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderList(orders: List<Order>, onUpdateStatus: (orderId: Long, newStatus: String) -> Unit) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No orders in this category.")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(orders, key = { it.id }) { order ->
                OrderCard(order = order, onUpdateStatus = onUpdateStatus)
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, onUpdateStatus: (orderId: Long, newStatus: String) -> Unit) {
    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.dailyOrderNumber}", style = MaterialTheme.typography.titleLarge)
            Text("Customer: ${order.customerName}", style = MaterialTheme.typography.bodyLarge)
            Text("Total: $${"%.2f".format(order.total)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            order.items.forEach { item ->
                val details = mutableListOf<String>()
                if (item.dineInQuantity > 0) details.add("${item.dineInQuantity}x Dine-in")
                if (item.takeawayQuantity > 0) details.add("${item.takeawayQuantity}x Takeaway")
                Text("${item.menuItemName} (${details.joinToString()})")
                if (item.specialInstructions != null) {
                    Text("  ↳ Instructions: ${item.specialInstructions}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            OrderCardActions(order = order, onUpdateStatus = onUpdateStatus)
        }
    }
}

@Composable
fun OrderCardActions(order: Order, onUpdateStatus: (orderId: Long, newStatus: String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        when (order.status) {
            "Pending" -> {
                Button(onClick = { onUpdateStatus(order.id, "Accepted") }) { Text("Accept") }
                Button(onClick = { onUpdateStatus(order.id, "Rejected") }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Reject") }
            }
            "Accepted" -> {
                Button(onClick = { onUpdateStatus(order.id, "Completed") }) { Text("Mark as Completed") }
            }
            else -> {
                // No actions for "Completed" or "Rejected" orders
            }
        }
    }
}
