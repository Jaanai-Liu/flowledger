package com.flowledger.app.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flowledger.app.ui.category.CategoryManageScreen
import com.flowledger.app.ui.dashboard.DashboardScreen
import com.flowledger.app.ui.lock.LockScreen
import com.flowledger.app.ui.recurring.RecurringRuleEditScreen
import com.flowledger.app.ui.recurring.RecurringRuleListScreen
import com.flowledger.app.ui.settings.AboutScreen
import com.flowledger.app.ui.settings.BackupRestoreScreen
import com.flowledger.app.ui.settings.SecurityScreen
import com.flowledger.app.ui.settings.SettingsScreen
import com.flowledger.app.ui.statistics.StatisticsScreen
import com.flowledger.app.ui.transaction.AddEditTransactionScreen
import com.flowledger.app.ui.transaction.TransactionDetailScreen
import com.flowledger.app.ui.transaction.TransactionListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(Screen.Lock.route) {
            LockScreen(
                onUnlocked = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Lock.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTransaction = { id ->
                    navController.navigate(Screen.TransactionDetail.createRoute(id))
                },
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddTransaction.createRoute())
                }
            )
        }

        composable(Screen.Transactions.route) {
            TransactionListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.TransactionDetail.createRoute(id))
                },
                onNavigateToAdd = {
                    navController.navigate(Screen.AddTransaction.createRoute())
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToCategories = { navController.navigate(Screen.Categories.route) },
                onNavigateToPaymentMethods = { navController.navigate(Screen.PaymentMethods.route) },
                onNavigateToRecurring = { navController.navigate(Screen.RecurringRules.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: -1L
            AddEditTransactionScreen(
                transactionId = if (transactionId == -1L) null else transactionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            TransactionDetailScreen(
                transactionId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { tid ->
                    navController.navigate(Screen.AddTransaction.createRoute(tid))
                }
            )
        }

        composable(Screen.Categories.route) {
            CategoryManageScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PaymentMethods.route) {
            // PaymentMethodManageScreen will be created later
            com.flowledger.app.ui.category.PaymentMethodManageScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RecurringRules.route) {
            RecurringRuleListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.RecurringRuleDetail.createRoute(id))
                },
                onNavigateToAdd = {
                    navController.navigate(Screen.AddRecurringRule.route)
                }
            )
        }

        composable(Screen.AddRecurringRule.route) {
            RecurringRuleEditScreen(
                ruleId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.RecurringRuleDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            RecurringRuleEditScreen(
                ruleId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBackup = { navController.navigate(Screen.BackupRestore.route) },
                onNavigateToSecurity = { navController.navigate(Screen.Security.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.BackupRestore.route) {
            BackupRestoreScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Security.route) {
            SecurityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun ProfileScreen(
    onNavigateToAccounts: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToRecurring: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    com.flowledger.app.ui.settings.ProfileScreen(
        onNavigateToAccounts = onNavigateToAccounts,
        onNavigateToCategories = onNavigateToCategories,
        onNavigateToPaymentMethods = onNavigateToPaymentMethods,
        onNavigateToRecurring = onNavigateToRecurring,
        onNavigateToSettings = onNavigateToSettings
    )
}
