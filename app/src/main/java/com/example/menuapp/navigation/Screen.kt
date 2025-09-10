package com.example.menuapp.navigation

sealed class Screen(val route: String) {
    object RoleSelection : Screen("role_selection_screen")
    object RestaurantSelection : Screen("restaurant_selection_screen")
    object RestaurantFlow : Screen("restaurant_flow/{restaurantId}") {
        fun createRoute(restaurantId: Long) = "restaurant_flow/$restaurantId"
    }
    object Menu : Screen("menu_screen")
    object Cart : Screen("cart_screen")
    object Invoice : Screen("invoice_screen")
    object AdminFlow : Screen("admin_flow")
    object AdminLogin : Screen("admin_login_screen")
    object AdminDashboard : Screen("admin_dashboard_screen")
    object Management : Screen("management_screen")
}
