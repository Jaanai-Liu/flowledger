package com.flowledger.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Lock : Screen("lock")
    data object Dashboard : Screen("dashboard")
    data object Transactions : Screen("transactions")
    data object Statistics : Screen("statistics")
    data object Profile : Screen("profile")
    data object AddTransaction : Screen("transaction/add?transactionId={transactionId}") {
        fun createRoute(transactionId: Long? = null) =
            if (transactionId != null) "transaction/add?transactionId=$transactionId"
            else "transaction/add"
    }
    data object TransactionDetail : Screen("transaction/{id}") {
        fun createRoute(id: Long) = "transaction/$id"
    }
    data object Accounts : Screen("accounts")
    data object AddAccount : Screen("accounts/add")
    data object AccountDetail : Screen("accounts/{id}") {
        fun createRoute(id: Long) = "accounts/$id"
    }
    data object Categories : Screen("categories")
    data object PaymentMethods : Screen("payment-methods")
    data object RecurringRules : Screen("recurring")
    data object AddRecurringRule : Screen("recurring/add")
    data object RecurringRuleDetail : Screen("recurring/{id}") {
        fun createRoute(id: Long) = "recurring/$id"
    }
    data object Settings : Screen("settings")
    data object BackupRestore : Screen("settings/backup")
    data object Security : Screen("settings/security")
    data object About : Screen("settings/about")
}

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("首页", Icons.Filled.Receipt, Icons.Outlined.Home, Screen.Dashboard.route),
    BottomNavItem("账单", Icons.Filled.Receipt, Icons.Outlined.Receipt, Screen.Transactions.route),
    BottomNavItem("统计", Icons.Filled.PieChart, Icons.Outlined.PieChart, Screen.Statistics.route),
    BottomNavItem("我的", Icons.Filled.Receipt, Icons.Outlined.Person, Screen.Profile.route)
)
