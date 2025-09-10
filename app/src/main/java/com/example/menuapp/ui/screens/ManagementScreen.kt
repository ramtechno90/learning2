package com.example.menuapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.menuapp.data.models.Category
import com.example.menuapp.data.models.MenuItem
import com.example.menuapp.data.models.Restaurant
import com.example.menuapp.ui.theme.SwiggyOrange
import com.example.menuapp.viewmodels.ManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(vm: ManagementViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Categories", "Menu Items", "Settings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Management") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                indicator = { tabPositions -> TabRowDefaults.Indicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = SwiggyOrange) }
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
                    when (selectedTabIndex) {
                        0 -> CategoryManagementTab(uiState.categories, vm::addCategory, vm::updateCategory, vm::deleteCategory)
                        1 -> MenuItemManagementTab(uiState.menuItems, uiState.categories, vm::updateMenuItemStock, vm::deleteMenuItem, vm::addMenuItem, vm::updateMenuItem)
                        2 -> uiState.restaurant?.let { SettingsManagementTab(it, vm::saveSettings) { bytes, ext -> vm.uploadLogo(bytes, ext) } }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryManagementTab(categories: List<Category>, onAddCategory: (String) -> Unit, onUpdateCategory: (Category) -> Unit, onDeleteCategory: (Long) -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Category?>(null) }

    if (showAddDialog) {
        NameEditDialog("Add Category", "", { showAddDialog = false }) { onAddCategory(it); showAddDialog = false }
    }
    showEditDialog?.let { cat -> NameEditDialog("Rename Category", cat.name, { showEditDialog = null }) { onUpdateCategory(cat.copy(name = it)); showEditDialog = null } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = SwiggyOrange) {
                Icon(Icons.Default.Add, "Add Category", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories, key = { it.id }) { category ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(category.name, style = MaterialTheme.typography.bodyLarge)
                    Row {
                        TextButton(onClick = { showEditDialog = category }) { Text("Rename") }
                        TextButton(onClick = { onDeleteCategory(category.id) }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) { Text("Delete") }
                    }
                }
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEditDialog(title: String, initialValue: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Name") }, singleLine = true) },
        confirmButton = { Button(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) { Text("Confirm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun MenuItemManagementTab(menuItems: List<MenuItem>, categories: List<Category>, onUpdateStock: (MenuItem, Boolean) -> Unit, onDeleteItem: (Long) -> Unit, onAddItem: (MenuItem) -> Unit, onUpdateItem: (MenuItem) -> Unit) {
    var showDialog by remember { mutableStateOf<MenuItem?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    showDialog?.let { item ->
        AddEditMenuItemDialog(item, isEditing, categories, { showDialog = null }) { updated ->
            if (isEditing) onUpdateItem(updated) else onAddItem(updated)
            showDialog = null
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isEditing = false; showDialog = MenuItem(0, "", 0.0, categories.firstOrNull()?.id ?: 0, 0) },
                containerColor = SwiggyOrange
            ) { Icon(Icons.Default.Add, "Add Menu Item", tint = Color.White) }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(menuItems, key = { it.id }) { item ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.name, modifier = Modifier.weight(1f))
                    Switch(checked = item.inStock, onCheckedChange = { onUpdateStock(item, it) }, colors = SwitchDefaults.colors(checkedThumbColor = SwiggyOrange))
                    TextButton(onClick = { isEditing = true; showDialog = item }) { Text("Edit") }
                    TextButton(onClick = { onDeleteItem(item.id) }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) { Text("Delete") }
                }
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMenuItemDialog(item: MenuItem, isEditing: Boolean, categories: List<Category>, onDismiss: () -> Unit, onConfirm: (MenuItem) -> Unit) {
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
                OutlinedTextField(name, { name = it }, label = { Text("Name") })
                OutlinedTextField(price, { price = it }, label = { Text("Price") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(description, { description = it }, label = { Text("Description") })
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
                    OutlinedTextField(selectedCategory?.name ?: "Select Category", {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) }, modifier = Modifier.menuAnchor())
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        categories.forEach { category -> DropdownMenuItem(text = { Text(category.name) }, onClick = { selectedCategory = category; categoryExpanded = false }) }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(item.copy(name = name, price = price.toDoubleOrNull() ?: 0.0, description = description, category = selectedCategory?.id ?: 0)) }) { Text("Confirm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SettingsManagementTab(restaurant: Restaurant, onSave: (String, Double) -> Unit, onUploadLogo: (ByteArray, String) -> Unit) {
    var name by remember(restaurant.name) { mutableStateOf(restaurant.name) }
    var parcelCharge by remember(restaurant.universalParcelCharge) { mutableStateOf(restaurant.universalParcelCharge.toString()) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                onUploadLogo(stream.readBytes(), context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg")
            }
        }
    }
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text("Restaurant Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(parcelCharge, { parcelCharge = it }, label = { Text("Universal Parcel Charge") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors()) {
            Text("Upload New Logo", color = SwiggyOrange)
        }
        Button(onClick = { onSave(name, parcelCharge.toDoubleOrNull() ?: 0.0) }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = SwiggyOrange)) {
            Text("Save Settings")
        }
    }
}
