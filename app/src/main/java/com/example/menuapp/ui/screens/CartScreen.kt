package com.example.menuapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            TopAppBar(title = { Text("Your Cart") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.lineItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Your cart is empty.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.lineItems, key = { it.menuItem.id }) { item ->
                        CartItemRow(
                            item = item,
                            onDineInQuantityChange = { cartViewModel.updateDineInQuantity(item.menuItem.id, it) },
                            onTakeawayQuantityChange = { cartViewModel.updateTakeawayQuantity(item.menuItem.id, it) },
                            onInstructionsChange = { cartViewModel.updateSpecialInstructions(item.menuItem.id, it) }
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                OrderSummary(
                    subtotal = uiState.subtotal,
                    parcelCharges = uiState.parcelCharges,
                    total = uiState.total,
                    onCheckoutClick = onNavigateToInvoice
                )
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
        Text(text = item.menuItem.name, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        QuantitySelector(
            label = "Dine-in",
            quantity = item.dineInQuantity,
            onQuantityChange = onDineInQuantityChange
        )

        QuantitySelector(
            label = "Takeaway",
            quantity = item.takeawayQuantity,
            onQuantityChange = onTakeawayQuantityChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = item.specialInstructions,
            onValueChange = onInstructionsChange,
            label = { Text("Special Instructions") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun QuantitySelector(label: String, quantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onQuantityChange(quantity - 1) }, enabled = quantity > 0) {
                Text("-")
            }
            Text(text = quantity.toString(), modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
            IconButton(onClick = { onQuantityChange(quantity + 1) }) {
                Text("+")
            }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SummaryRow("Subtotal:", "$${"%.2f".format(subtotal)}")
            SummaryRow("Parcel Charges:", "$${"%.2f".format(parcelCharges)}")
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            SummaryRow("Grand Total:", "$${"%.2f".format(total)}", isBold = true)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCheckoutClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(text = value, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    }
}
