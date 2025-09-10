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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.menuapp.viewmodels.CartViewModel
import com.example.menuapp.viewmodels.InvoiceViewModel
import com.example.menuapp.viewmodels.OrderPlacementState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    cartViewModel: CartViewModel,
    invoiceViewModel: InvoiceViewModel = viewModel(),
    onOrderPlacedSuccessfully: () -> Unit
) {
    val cartState by cartViewModel.uiState.collectAsState()
    val customerName by invoiceViewModel.customerName.collectAsState()
    val orderPlacementState by invoiceViewModel.orderPlacementState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Confirm Your Order") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Order Summary", style = MaterialTheme.typography.headlineSmall)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartState.lineItems) { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.totalQuantity}x ${item.menuItem.name}")
                        Text("$${"%.2f".format(item.menuItem.price * item.totalQuantity)}")
                    }
                }
                item {
                    Column {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        SummaryRow("Subtotal:", "$${"%.2f".format(cartState.subtotal)}")
                        SummaryRow("Parcel Charges:", "$${"%.2f".format(cartState.parcelCharges)}")
                        SummaryRow("Grand Total:", "$${"%.2f".format(cartState.total)}", isBold = true)
                    }
                }
            }

            OutlinedTextField(
                value = customerName,
                onValueChange = { invoiceViewModel.onCustomerNameChange(it) },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { invoiceViewModel.placeOrder(cartState) },
                enabled = customerName.isNotBlank() && orderPlacementState is OrderPlacementState.Idle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Place Order")
            }
        }

        when (val state = orderPlacementState) {
            is OrderPlacementState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is OrderPlacementState.Success -> {
                SuccessDialog(
                    orderNumber = state.order.dailyOrderNumber,
                    onDismiss = {
                        cartViewModel.clearCart()
                        invoiceViewModel.resetOrderState()
                        onOrderPlacedSuccessfully()
                    }
                )
            }
            is OrderPlacementState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { invoiceViewModel.resetOrderState() }
                )
            }
            else -> {}
        }
    }
}

@Composable
fun SuccessDialog(orderNumber: Long, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Order Placed!") },
        text = { Text("Your order number is #$orderNumber. Thank you!") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Order Failed") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
