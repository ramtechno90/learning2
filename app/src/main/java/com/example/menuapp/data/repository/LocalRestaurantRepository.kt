package com.example.menuapp.data.repository

import com.example.menuapp.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * An in-memory implementation of the repository for local development and testing.
 * This class simulates a backend database using mutable lists and flows.
 */
class LocalRestaurantRepository {

    // --- In-Memory Data Store ---
    private val restaurants = mutableListOf(
        Restaurant(id = 1, name = "The Burger Palace", universalParcelCharge = 1.50, logoUrl = "https://firebasestorage.googleapis.com/v0/b/fir-chat-6d859.appspot.com/o/restaurants%2Fburger.png?alt=media&token=8b3758da-e646-4464-8557-c157f12239f2"),
        Restaurant(id = 2, name = "Pizza Heaven", universalParcelCharge = 2.00, logoUrl = "https://firebasestorage.googleapis.com/v0/b/fir-chat-6d859.appspot.com/o/restaurants%2Fpizza.png?alt=media&token=e1150897-47b2-4879-a799-23c3b313d332")
    )

    private val categories = MutableStateFlow(mutableListOf(
        Category(id = 1, name = "Burgers", restaurantId = 1),
        Category(id = 2, name = "Sides", restaurantId = 1),
        Category(id = 3, name = "Classic Pizzas", restaurantId = 2),
        Category(id = 4, name = "Gourmet Pizzas", restaurantId = 2)
    ))

    private val menuItems = MutableStateFlow(mutableListOf(
        MenuItem(id = 1, name = "Classic Burger", price = 8.99, description = "A juicy beef patty with lettuce, tomato, and our special sauce.", category = 1, restaurantId = 1),
        MenuItem(id = 2, name = "Cheese Burger", price = 9.99, description = "Our classic burger with a slice of cheddar cheese.", category = 1, restaurantId = 1, inStock = false),
        MenuItem(id = 3, name = "Fries", price = 3.50, description = "Crispy golden fries.", category = 2, restaurantId = 1),
        MenuItem(id = 4, name = "Margherita Pizza", price = 12.00, description = "Classic pizza with tomato, mozzarella, and basil.", category = 3, restaurantId = 2),
        MenuItem(id = 5, name = "Pepperoni Pizza", price = 14.00, description = "The all-time favorite.", category = 3, restaurantId = 2),
        MenuItem(id = 6, name = "Truffle Mushroom Pizza", price = 18.00, description = "Gourmet pizza with truffle oil and assorted mushrooms.", category = 4, restaurantId = 2)
    ))

    private val orders = MutableStateFlow(mutableListOf<Order>())
    private var orderIdCounter = 1L
    private var isUserLoggedIn = false
    data class FakeUser(val id: String)

    // --- Public API ---

    suspend fun getRestaurant(restaurantId: Long): Restaurant? {
        return restaurants.find { it.id == restaurantId }
    }

    suspend fun getCategories(restaurantId: Long): List<Category> {
        return categories.value.filter { it.restaurantId == restaurantId }
    }

    suspend fun getMenuItems(restaurantId: Long): List<MenuItem> {
        return menuItems.value.filter { it.restaurantId == restaurantId }
    }

    suspend fun placeOrder(order: Order): Order {
        val newOrder = order.copy(
            id = orderIdCounter++,
            status = "Pending",
            dailyOrderNumber = (orders.value.count { it.status == "Pending" } + 1).toLong()
        )
        val currentOrders = orders.value.toMutableList()
        currentOrders.add(newOrder)
        orders.value = currentOrders
        return newOrder
    }

    // --- Admin Functions ---

    suspend fun signIn(email: String, password: String) {
        if (email != "admin@example.com" || password != "password") {
            throw Exception("Invalid credentials. Use admin@example.com and password.")
        }
        isUserLoggedIn = true
    }

    suspend fun signOut() {
        isUserLoggedIn = false
    }

    fun getCurrentUser(): FakeUser? {
        return if (isUserLoggedIn) FakeUser(id = "fake-admin-user-id") else null
    }

    suspend fun getRestaurantIdForUser(userId: String): Long? = 1L

    fun getOrdersFlow(restaurantId: Long): Flow<List<Order>> {
        return orders.asStateFlow().map { orderList ->
            orderList.filter { it.restaurantId == restaurantId }
        }
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: String) {
        val currentOrders = orders.value.toMutableList()
        val orderIndex = currentOrders.indexOfFirst { it.id == orderId }
        if (orderIndex != -1) {
            currentOrders[orderIndex] = currentOrders[orderIndex].copy(status = newStatus)
            orders.value = currentOrders
        }
    }

    // --- Management Functions ---

    suspend fun addMenuItem(item: MenuItem) {
        val currentItems = menuItems.value.toMutableList()
        currentItems.add(item.copy(id = (menuItems.value.maxOfOrNull { it.id } ?: 0) + 1))
        menuItems.value = currentItems
    }

    suspend fun updateMenuItem(item: MenuItem) {
        val currentItems = menuItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            currentItems[index] = item
            menuItems.value = currentItems
        }
    }

    suspend fun deleteMenuItem(itemId: Long) {
        menuItems.value = menuItems.value.filter { it.id != itemId }.toMutableList()
    }

    suspend fun addCategory(category: Category) {
        val currentCategories = categories.value.toMutableList()
        currentCategories.add(category.copy(id = (categories.value.maxOfOrNull { it.id } ?: 0) + 1))
        categories.value = currentCategories
    }

    suspend fun updateCategory(category: Category) {
        val currentCategories = categories.value.toMutableList()
        val index = currentCategories.indexOfFirst { it.id == category.id }
        if (index != -1) {
            currentCategories[index] = category
            categories.value = currentCategories
        }
    }

    suspend fun deleteCategory(categoryId: Long) {
        categories.value = categories.value.filter { it.id != categoryId }.toMutableList()
    }

    suspend fun updateRestaurantDetails(id: Long, name: String, universalParcelCharge: Double) {
        val index = restaurants.indexOfFirst { it.id == id }
        if (index != -1) {
            restaurants[index] = restaurants[index].copy(name = name, universalParcelCharge = universalParcelCharge)
        }
    }

    suspend fun updateRestaurantLogoUrl(restaurantId: Long, logoUrl: String) {
        val index = restaurants.indexOfFirst { it.id == restaurantId }
        if (index != -1) {
            restaurants[index] = restaurants[index].copy(logoUrl = logoUrl)
        }
    }
}
