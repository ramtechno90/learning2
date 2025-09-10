package com.example.menuapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.menuapp.data.repository.LocalRestaurantRepository
import com.example.menuapp.ui.screens.*
import com.example.menuapp.viewmodels.CartViewModel
import com.example.menuapp.viewmodels.CartViewModelFactory
import com.example.menuapp.viewmodels.MenuViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val localRestaurantRepository = remember { LocalRestaurantRepository() }

    NavHost(
        navController = navController,
        startDestination = Screen.RoleSelection.route
    ) {
        composable(route = Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onCustomerSelected = { navController.navigate(Screen.RestaurantSelection.route) },
                onAdminSelected = { navController.navigate(Screen.AdminFlow.route) }
            )
        }

        composable(route = Screen.RestaurantSelection.route) {
            RestaurantSelectionScreen(
                onNavigateToMenu = { restaurantId ->
                    navController.navigate(Screen.RestaurantFlow.createRoute(restaurantId))
                }
            )
        }

        customerRestaurantFlow(navController, localRestaurantRepository)
        adminFlow(navController)
    }
}

private fun NavGraphBuilder.customerRestaurantFlow(
    navController: NavHostController,
    repository: LocalRestaurantRepository
) {
    navigation(
        startDestination = Screen.Menu.route,
        route = Screen.RestaurantFlow.route,
        arguments = listOf(androidx.navigation.navArgument("restaurantId") {
            type = androidx.navigation.NavType.LongType
        })
    ) {
        composable(route = Screen.Menu.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.RestaurantFlow.route) }
            val restaurantId = parentEntry.arguments?.getLong("restaurantId")!!

            val cartViewModel: CartViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = CartViewModelFactory(restaurantId, repository)
            )

            // The MenuViewModel gets the SavedStateHandle automatically from the NavBackStackEntry
            val menuViewModel: MenuViewModel = viewModel(backStackEntry)

            MenuScreen(
                menuViewModel = menuViewModel,
                cartViewModel = cartViewModel,
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(route = Screen.Cart.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.RestaurantFlow.route) }
            val restaurantId = parentEntry.arguments?.getLong("restaurantId")!!

            val cartViewModel: CartViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = CartViewModelFactory(restaurantId, repository)
            )

            CartScreen(
                cartViewModel = cartViewModel,
                onNavigateToInvoice = { navController.navigate(Screen.Invoice.route) }
            )
        }

        composable(route = Screen.Invoice.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.RestaurantFlow.route) }
            val restaurantId = parentEntry.arguments?.getLong("restaurantId")!!

            val cartViewModel: CartViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = CartViewModelFactory(restaurantId, repository)
            )

            InvoiceScreen(
                cartViewModel = cartViewModel,
                onOrderPlacedSuccessfully = {
                    navController.popBackStack(Screen.RestaurantSelection.route, false)
                }
            )
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
