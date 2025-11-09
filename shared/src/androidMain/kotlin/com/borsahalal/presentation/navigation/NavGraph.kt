package com.borsahalal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.borsahalal.presentation.ui.screens.dashboard.DashboardScreenEnhanced
import com.borsahalal.presentation.ui.screens.profile.ProfileManagementScreen
import com.borsahalal.presentation.ui.screens.reports.ReportsScreenEnhanced
import com.borsahalal.presentation.ui.screens.settings.SettingsScreen
import com.borsahalal.presentation.ui.screens.stocks.StockDetailScreen
import com.borsahalal.presentation.ui.screens.stocks.StockListScreenEnhanced
import com.borsahalal.presentation.ui.screens.transactions.AddTransactionScreen
import com.borsahalal.presentation.ui.screens.transactions.TransactionListScreenEnhanced

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreenEnhanced()
        }

        // Stocks
        composable(Screen.Stocks.route) {
            StockListScreenEnhanced(
                onStockClick = { stockId ->
                    navController.navigate(Screen.StockDetail.createRoute(stockId))
                }
            )
        }

        // Stock Detail
        composable(
            route = Screen.StockDetail.route,
            arguments = listOf(
                navArgument("stockId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val stockId = backStackEntry.arguments?.getLong("stockId") ?: 0L
            StockDetailScreen(
                stockId = stockId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Transactions
        composable(Screen.Transactions.route) {
            TransactionListScreenEnhanced(
                onAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onTransactionClick = { transactionId ->
                    // TODO: Navigate to transaction detail/edit
                }
            )
        }

        // Add Transaction
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Reports
        composable(Screen.Reports.route) {
            ReportsScreenEnhanced()
        }

        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToProfiles = {
                    navController.navigate(Screen.ProfileManagement.route)
                }
            )
        }

        // Profile Management
        composable(Screen.ProfileManagement.route) {
            ProfileManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
