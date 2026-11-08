package merail.tools.packages.impl

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object PackagesRoute

fun NavController.navigateToPackages(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = PackagesRoute,
        builder = {
            launchSingleTop = true
            navOptions()
        },
    )
}

fun NavGraphBuilder.packagesScreen() {
    composable<PackagesRoute> {
        PackagesScreen()
    }
}