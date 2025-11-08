package com.borsahalal.presentation.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Stocks : Screen("stocks")
    object StockDetail : Screen("stock/{stockId}") {
        fun createRoute(stockId: Long) = "stock/$stockId"
    }
    object AddTransaction : Screen("add_transaction")
    object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit_transaction/$transactionId"
    }
    object Transactions : Screen("transactions")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object ProfileManagement : Screen("profile_management")
}

sealed class BottomNavItem(
    val screen: Screen,
    val title: String,
    val icon: String // Using string for icon name, can be replaced with actual icon composables
) {
    object Dashboard : BottomNavItem(
        screen = Screen.Dashboard,
        title = "Dashboard",
        icon = "home"
    )

    object Stocks : BottomNavItem(
        screen = Screen.Stocks,
        title = "Stocks",
        icon = "list"
    )

    object Transactions : BottomNavItem(
        screen = Screen.Transactions,
        title = "Transactions",
        icon = "swap"
    )

    object Reports : BottomNavItem(
        screen = Screen.Reports,
        title = "Reports",
        icon = "chart"
    )

    object Settings : BottomNavItem(
        screen = Screen.Settings,
        title = "Settings",
        icon = "settings"
    )

    companion object {
        val items = listOf(Dashboard, Stocks, Transactions, Reports, Settings)
    }
}
