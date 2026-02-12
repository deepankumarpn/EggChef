package deepankumarpn.github.io.eggchef.navigation

sealed class Screen(val route: String) {
    data object BoilTimer : Screen("boil_timer")
    data object Identifier : Screen("identifier")
}
