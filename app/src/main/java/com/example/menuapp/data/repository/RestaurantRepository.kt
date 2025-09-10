package com.example.menuapp.data.repository

import com.example.menuapp.data.models.Category
import com.example.menuapp.data.models.MenuItem
import com.example.menuapp.data.models.Order
import com.example.menuapp.data.models.Restaurant
import com.example.menuapp.data.supabase.SupabaseManager
import io.github.jan_tennert.supabase.gotrue.GoTrue
import io.github.jan_tennert.supabase.gotrue.gotrue
import io.github.jan_tennert.supabase.gotrue.providers.builtin.Email
import io.github.jan_tennert.supabase.postgrest.postgrest
import io.github.jan_tennert.supabase.postgrest.result.PostgrestResult

/**
 * Repository for handling all data operations related to restaurants, menus, and orders.
 *
 * This class abstracts the data source (Supabase) from the rest of the application.
 */
class RestaurantRepository {

    private val postgrest = SupabaseManager.client.postgrest
    private val auth: GoTrue = SupabaseManager.client.gotrue

    /**
     * Fetches a single restaurant's details by its ID.
     *
     * @param restaurantId The ID of the restaurant to fetch.
     * @return The [Restaurant] object or null if not found.
     */
    suspend fun getRestaurant(restaurantId: Long): Restaurant? {
        return try {
            postgrest.from("restaurants")
                .select {
                    filter {
                        eq("id", restaurantId)
                    }
                }
                .decodeSingleOrNull<Restaurant>()
        } catch (e: Exception) {
            // Log error or handle it as per app's error policy
            println("Error fetching restaurant: ${e.message}")
            null
        }
    }

    /**
     * Fetches all menu categories for a given restaurant.
     *
     * @param restaurantId The ID of the restaurant.
     * @return A list of [Category] objects.
     */
    suspend fun getCategories(restaurantId: Long): List<Category> {
        return try {
            postgrest.from("categories")
                .select {
                    filter {
                        eq("restaurant_id", restaurantId)
                    }
                }
                .decodeList<Category>()
        } catch (e: Exception) {
            println("Error fetching categories: ${e.message}")
            emptyList()
        }
    }

    /**
     * Fetches all menu items for a given restaurant.
     *
     * @param restaurantId The ID of the restaurant.
     * @return A list of [MenuItem] objects.
     */
    suspend fun getMenuItems(restaurantId: Long): List<MenuItem> {
        return try {
            postgrest.from("menu_items")
                .select {
                    filter {
                        eq("restaurant_id", restaurantId)
                    }
                }
                .decodeList<MenuItem>()
        } catch (e: Exception) {
            println("Error fetching menu items: ${e.message}")
            emptyList()
        }
    }

    /**
     * Inserts a new order into the database.
     *
     * @param order The [Order] object to be inserted. Note that the id, status, and daily_order_number
     *              are typically set by the database, so the input object might not need them.
     * @return The newly created [Order] object as returned by the database.
     */
    suspend fun placeOrder(order: Order): Order? {
        return try {
            // The 'orders' table will receive the 'items' list and it will be stored as JSONB.
            // We expect the database to return the newly created row.
            postgrest.from("orders")
                .insert(order, returning = io.github.jan_tennert.supabase.postgrest.query.Returning.REPRESENTATION)
                .decodeSingleOrNull<Order>()
        } catch (e: Exception) {
            println("Error placing order: ${e.message}")
            null
        }
    }

    // --- Auth Functions ---

    /**
     * Signs in a user with their email and password. Throws an exception on failure.
     */
    suspend fun signIn(email: String, password: String) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    /**
     * Signs out the current user.
     */
    suspend fun signOut() {
        auth.signOut()
    }

    /**
     * Gets the current authenticated user, if any.
     */
    fun getCurrentUser(): io.github.jan_tennert.supabase.gotrue.user.User? {
        return auth.currentUserOrNull()
    }

    // --- Admin/Dashboard Functions ---

    /**
     * Fetches the restaurant ID associated with a given user ID.
     */
    suspend fun getRestaurantIdForUser(userId: String): Long? {
        return try {
            postgrest.from("restaurant_users")
                .select {
                    filter { eq("user_id", userId) }
                    single() // Expect only one row
                }
                .decodeSingleOrNull<com.example.menuapp.data.models.RestaurantUser>()?.restaurantId
        } catch (e: Exception) {
            println("Error fetching restaurant for user: ${e.message}")
            null
        }
    }

    /**
     * Subscribes to real-time changes for orders of a specific restaurant.
     * @return A Flow that emits the full list of orders whenever a change occurs.
     */
    fun getOrdersFlow(restaurantId: Long): kotlinx.coroutines.flow.Flow<List<Order>> {
        val channel = SupabaseManager.client.realtime.channel("orders_for_restaurant_$restaurantId")

        // This flow will emit a value every time a change happens in the 'orders' table
        val changeFlow = channel.postgresChangeFlow<io.github.jan_tennert.supabase.realtime.PostgresAction>(schema = "public") {
            table = "orders"
            filter = "restaurant_id=eq.$restaurantId"
        }

        // We map the change event to a fresh fetch of the entire order list.
        // .onStart ensures we get the initial list as soon as we start collecting.
        return changeFlow
            .map {
                getOrdersForRestaurant(restaurantId) // Re-fetch the list on any change
            }
            .onStart {
                emit(getOrdersForRestaurant(restaurantId)) // Emit the initial list
            }
    }

    /**
     * Helper to fetch all orders for a restaurant.
     */
    private suspend fun getOrdersForRestaurant(restaurantId: Long): List<Order> {
        return try {
            postgrest.from("orders")
                .select {
                    filter { eq("restaurant_id", restaurantId) }
                }
                .decodeList<Order>()
        } catch (e: Exception) {
            println("Error fetching orders: ${e.message}")
            emptyList()
        }
    }

    /**
     * Updates the status of a specific order.
     */
    suspend fun updateOrderStatus(orderId: Long, newStatus: String) {
        try {
            postgrest.from("orders")
                .update({
                    set("status", newStatus)
                }) {
                    filter { eq("id", orderId) }
                }
        } catch (e: Exception) {
            println("Error updating order status: ${e.message}")
            // Optionally re-throw or handle error
        }
    }

    // --- Menu & Category Management ---

    suspend fun addMenuItem(item: com.example.menuapp.data.models.MenuItem) = postgrest.from("menu_items").insert(item)
    suspend fun updateMenuItem(item: com.example.menuapp.data.models.MenuItem) = postgrest.from("menu_items").update(item) { filter { eq("id", item.id) } }
    suspend fun deleteMenuItem(itemId: Long) = postgrest.from("menu_items").delete { filter { eq("id", itemId) } }

    suspend fun addCategory(category: com.example.menuapp.data.models.Category) = postgrest.from("categories").insert(category)
    suspend fun updateCategory(category: com.example.menuapp.data.models.Category) = postgrest.from("categories").update(category) { filter { eq("id", category.id) } }
    suspend fun deleteCategory(categoryId: Long) = postgrest.from("categories").delete { filter { eq("id", categoryId) } }

    // --- Settings Management ---

    suspend fun updateRestaurantDetails(id: Long, name: String, universalParcelCharge: Double) {
        postgrest.from("restaurants").update({
            set("name", name)
            set("universal_parcel_charge", universalParcelCharge)
        }) {
            filter { eq("id", id) }
        }
    }

    suspend fun uploadLogo(restaurantId: Long, fileBytes: ByteArray, fileExtension: String): String {
        val path = "logos/restaurant_${restaurantId}_${System.currentTimeMillis()}.$fileExtension"
        SupabaseManager.client.storage.from("logos").upload(path, fileBytes)
        return SupabaseManager.client.storage.from("logos").publicUrl(path)
    }

    suspend fun updateRestaurantLogoUrl(restaurantId: Long, logoUrl: String) {
        postgrest.from("restaurants").update({
            set("logo_url", logoUrl)
        }) {
            filter { eq("id", restaurantId) }
        }
    }
}
