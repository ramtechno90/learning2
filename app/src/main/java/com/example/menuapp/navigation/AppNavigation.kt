package com.example.menuapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.menuapp.data.repository.RestaurantRepository
import com.example.menuapp.ui.screens.*
import com.example.menuapp.viewmodels.CartViewModel
import com.example.menuapp.viewmodels.CartViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.RoleSelection.route // New starting point
    ) {
        // Top-level role selection screen
        composable(route = Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onCustomerSelected = { navController.navigate(Screen.RestaurantSelection.route) },
                onAdminSelected = { navController.navigate(Screen.AdminLogin.route) }
            )
        }

        // Top-level screen for customer to select a restaurant
        composable(route = Screen.RestaurantSelection.route) {
            RestaurantSelectionScreen(
                onNavigateToMenu = { restaurantId ->
                    navController.navigate(Screen.RestaurantFlow.createRoute(restaurantId))
                }
            )
        }

        // Nested graph for the customer flow (menu, cart, invoice)
        customerRestaurantFlow(navController)

        // Nested graph for the admin flow (login, dashboard)
        adminFlow(navController)
    }
}

private fun NavGraphBuilder.customerRestaurantFlow(navController: NavHostController) {
    navigation(
        startDestination = Screen.Menu.route,
        route = Screen.RestaurantFlow.route,
        arguments = listOf(androidx.navigation.navArgument("restaurantId") {
            type = androidx.navigation.NavType.LongType
        })
    ) {
        composable(route = Screen.Menu.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.RestaurantFlow.route) }
            val cartViewModel: CartViewModel = viewModel(parentEntry, factory = CartViewModelFactory(parentEntry.arguments?.getLong("restaurantId")!!, RestaurantRepository()))
            MenuScreen(cartViewModel = cartViewModel, onNavigateToCart = { navController.navigate(Screen.Cart.route) })
        }

        composable(route = Screen.Cart.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.RestaurantFlow.route) }
            val cartViewModel: CartViewModel = viewModel(parentEntry, factory = CartViewModelFactory(parentEntry.arguments?.getLong("restaurantId")!!, RestaurantRepository()))
            CartScreen(cartViewModel = cartViewModel, onNavigateToInvoice = { navController.navigate(Screen.Invoice.route) })
        }

        composable(route = Screen.Invoice.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.RestaurantFlow.route) }
            val cartViewModel: CartViewModel = viewModel(parentEntry, factory = CartViewModelFactory(parentEntry.arguments?.getLong("restaurantId")!!, RestaurantRepository()))
            InvoiceScreen(cartViewModel = cartViewModel, onOrderPlacedSuccessfully = { navController.popBackStack(Screen.RestaurantSelection.route, false) })
        }
    }
}

private fun NavGraphBuilder.adminFlow(navController: NavHostController) {
    navigation(
        startDestination = Screen.AdminLogin.route,
        route = Screen.AdminFlow.route
    ) {
        composable(route = Screen.AdminLogin.route) {
            AdminLoginScreen(
                onLoginSuccess = {
                    // On success, navigate to the dashboard and clear the login screen from the back stack
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.AdminLogin.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onNavigateToManagement = { navController.navigate(Screen.Management.route) },
                onSignOut = {
                    // On sign out, navigate back to the role selection screen, clearing the entire back stack
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Management.route) {
            ManagementScreen()
        }
    }
}
