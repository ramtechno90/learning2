package com.example.menuapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.menuapp.data.models.Category
import com.example.menuapp.viewmodels.ManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    vm: ManagementViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Categories", "Menu Items", "Settings")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Restaurant Management") }) }
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
                    when (selectedTabIndex) {
                        0 -> CategoryManagementTab(
                            categories = uiState.categories,
                            onAddCategory = { vm.addCategory(it) },
                            onUpdateCategory = { vm.updateCategory(it) },
                            onDeleteCategory = { vm.deleteCategory(it) }
                        )
                        1 -> MenuItemManagementTab(
                            menuItems = uiState.menuItems,
                            categories = uiState.categories,
                            onUpdateStock = { item, inStock -> vm.updateMenuItemStock(item, inStock) },
                            onDeleteItem = { vm.deleteMenuItem(it) },
                            onAddItem = { vm.addMenuItem(it) },
                            onUpdateItem = { vm.updateMenuItem(it) }
                        )
                        2 -> SettingsManagementTab(
                            restaurant = uiState.restaurant!!,
                            onSave = { name, charge -> vm.saveSettings(name, charge) },
                            onUploadLogo = { bytes, ext -> vm.uploadLogo(bytes, ext) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsManagementTab(
    restaurant: com.example.menuapp.data.models.Restaurant,
    onSave: (String, Double) -> Unit,
    onUploadLogo: (ByteArray, String) -> Unit
) {
    var name by remember(restaurant.name) { mutableStateOf(restaurant.name) }
    var parcelCharge by remember(restaurant.universalParcelCharge) { mutableStateOf(restaurant.universalParcelCharge.toString()) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                val extension = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"
                onUploadLogo(bytes, extension)
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Restaurant Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = parcelCharge,
            onValueChange = { parcelCharge = it },
            label = { Text("Universal Parcel Charge") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Upload New Logo")
        }
        Button(
            onClick = { onSave(name, parcelCharge.toDoubleOrNull() ?: 0.0) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Settings")
        }
    }
}

@Composable
fun MenuItemManagementTab(
    menuItems: List<com.example.menuapp.data.models.MenuItem>,
    categories: List<Category>,
    onUpdateStock: (com.example.menuapp.data.models.MenuItem, Boolean) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onAddItem: (com.example.menuapp.data.models.MenuItem) -> Unit,
    onUpdateItem: (com.example.menuapp.data.models.MenuItem) -> Unit
) {
    var showDialog by remember { mutableStateOf<com.example.menuapp.data.models.MenuItem?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    if (showDialog != null) {
        AddEditMenuItemDialog(
            item = showDialog,
            isEditing = isEditing,
            categories = categories,
            onDismiss = { showDialog = null },
            onConfirm = { item ->
                if (isEditing) onUpdateItem(item) else onAddItem(item)
                showDialog = null
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = {
                isEditing = false
                showDialog = com.example.menuapp.data.models.MenuItem(id = 0, name = "", price = 0.0, category = categories.firstOrNull()?.id ?: 0, restaurantId = 0)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Menu Item")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(menuItems, key = { it.id }) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.name, modifier = Modifier.weight(1f))
                    Switch(checked = item.inStock, onCheckedChange = { onUpdateStock(item, it) })
                    TextButton(onClick = {
                        isEditing = true
                        showDialog = item
                    }) { Text("Edit") }
                    TextButton(onClick = { onDeleteItem(item.id) }) { Text("Delete") }
                }
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMenuItemDialog(
    item: com.example.menuapp.data.models.MenuItem,
    isEditing: Boolean,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onConfirm: (com.example.menuapp.data.models.MenuItem) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var price by remember { mutableStateOf(item.price.toString()) }
    var description by remember { mutableStateOf(item.description ?: "") }
    var selectedCategory by remember { mutableStateOf(categories.find { it.id == item.category }) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Menu Item" else "Add Menu Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })

                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "Select Category",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(text = { Text(category.name) }, onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedItem = item.copy(
                    name = name,
                    price = price.toDoubleOrNull() ?: 0.0,
                    description = description,
                    category = selectedCategory?.id ?: 0
                )
                onConfirm(updatedItem)
            }) { Text("Confirm") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun CategoryManagementTab(
    categories: List<Category>,
    onAddCategory: (String) -> Unit,
    onUpdateCategory: (Category) -> Unit,
    onDeleteCategory: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Category?>(null) }
    var categoryName by remember { mutableStateOf("") }

    if (showAddDialog) {
        NameEditDialog(
            title = "Add Category",
            initialValue = "",
            onDismiss = { showAddDialog = false },
            onConfirm = {
                onAddCategory(it)
                showAddDialog = false
            }
        )
    }

    if (showEditDialog != null) {
        NameEditDialog(
            title = "Rename Category",
            initialValue = showEditDialog?.name ?: "",
            onDismiss = { showEditDialog = null },
            onConfirm = { newName ->
                showEditDialog?.let { onUpdateCategory(it.copy(name = newName)) }
                showEditDialog = null
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { showAddDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Add New Category")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories, key = { it.id }) { category ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(category.name, style = MaterialTheme.typography.bodyLarge)
                    Row {
                        TextButton(onClick = { showEditDialog = category }) { Text("Rename") }
                        TextButton(onClick = { onDeleteCategory(category.id) }) { Text("Delete") }
                    }
                }
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEditDialog(
    title: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Name") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
