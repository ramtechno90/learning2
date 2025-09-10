package com.example.menuapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.menuapp.ui.theme.SwiggyOrange
import com.example.menuapp.viewmodels.CartLineItem
import com.example.menuapp.viewmodels.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onNavigateToInvoice: () -> Unit
) {
    val uiState by cartViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface)
            )
        },
        bottomBar = {
            if (uiState.lineItems.isNotEmpty()) {
                OrderSummary(
                    subtotal = uiState.subtotal,
                    parcelCharges = uiState.parcelCharges,
                    total = uiState.total,
                    onCheckoutClick = onNavigateToInvoice
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.lineItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Your cart is empty.") }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.lineItems, key = { it.menuItem.id }) { item ->
                        CartItemRow(
                            item = item,
                            onDineInQuantityChange = { cartViewModel.updateDineInQuantity(item.menuItem.id, it) },
                            onTakeawayQuantityChange = { cartViewModel.updateTakeawayQuantity(item.menuItem.id, it) },
                            onInstructionsChange = { cartViewModel.updateSpecialInstructions(item.menuItem.id, it) }
                        )
                        Divider(color = MaterialTheme.colorScheme.background)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartLineItem,
    onDineInQuantityChange: (Int) -> Unit,
    onTakeawayQuantityChange: (Int) -> Unit,
    onInstructionsChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(item.menuItem.name, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        QuantitySelector("Dine-in", item.dineInQuantity, onDineInQuantityChange)
        Spacer(modifier = Modifier.height(8.dp))
        QuantitySelector("Takeaway", item.takeawayQuantity, onTakeawayQuantityChange)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = item.specialInstructions,
            onValueChange = onInstructionsChange,
            label = { Text("Special Instructions (optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )
    }
}

@Composable
fun QuantitySelector(label: String, quantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { onQuantityChange(quantity - 1) },
                enabled = quantity > 0,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp),
                contentPadding = PaddingValues(0.dp)
            ) { Icon(Icons.Default.Remove, "Decrease quantity") }
            Text(quantity.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
            OutlinedButton(
                onClick = { onQuantityChange(quantity + 1) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp),
                contentPadding = PaddingValues(0.dp)
            ) { Icon(Icons.Default.Add, "Increase quantity") }
        }
    }
}

@Composable
fun OrderSummary(
    subtotal: Double,
    parcelCharges: Double,
    total: Double,
    onCheckoutClick: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            SummaryRow("Subtotal", "$${"%.2f".format(subtotal)}")
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow("Parcel Charges", "$${"%.2f".format(parcelCharges)}")
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            SummaryRow("Grand Total", "$${"%.2f".format(total)}", isBold = true, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCheckoutClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = SwiggyOrange)
            ) { Text("Proceed to Checkout") }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isBold: Boolean = false, style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, style = style, color = if(isBold) MaterialTheme.colorScheme.onSurface else Color.Gray)
        Text(value, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, style = style)
    }
}
