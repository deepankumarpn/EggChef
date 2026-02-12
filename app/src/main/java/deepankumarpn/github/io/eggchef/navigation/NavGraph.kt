package deepankumarpn.github.io.eggchef.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import deepankumarpn.github.io.eggchef.presentation.ui.screens.boiltimer.BoilTimerScreen
import deepankumarpn.github.io.eggchef.presentation.ui.screens.identifier.IdentifierScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.BoilTimer.route
    ) {
        composable(Screen.BoilTimer.route) {
            BoilTimerScreen(
                onNavigateToIdentifier = {
                    navController.navigate(Screen.Identifier.route)
                }
            )
        }
        composable(Screen.Identifier.route) {
            IdentifierScreen()
        }
    }
}
