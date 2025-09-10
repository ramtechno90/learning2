package com.example.menuapp.navigation

/**
 * A sealed class to define the navigation routes in the application in a type-safe way.
 */
sealed class Screen(val route: String) {
    // Top-level destinations
    object RoleSelection : Screen("role_selection_screen")

    // --- Customer Flow ---
    object RestaurantSelection : Screen("restaurant_selection_screen")

    /**
     * A nested graph for the flow after a restaurant has been selected.
     */
    object RestaurantFlow : Screen("restaurant_flow/{restaurantId}") {
        fun createRoute(restaurantId: Long) = "restaurant_flow/$restaurantId"
    }
    // Destinations within the RestaurantFlow
    object Menu : Screen("menu_screen")
    object Cart : Screen("cart_screen")
    object Invoice : Screen("invoice_screen")


    // --- Admin Flow ---
    /**
     * A nested graph for the authenticated admin flow.
     */
    object AdminFlow : Screen("admin_flow") {
        // This could be expanded if the admin flow had multiple start points
    }
    // Destinations within the AdminFlow
    object AdminLogin : Screen("admin_login_screen")
    object AdminDashboard : Screen("admin_dashboard_screen")
    object Management : Screen("management_screen")
}
