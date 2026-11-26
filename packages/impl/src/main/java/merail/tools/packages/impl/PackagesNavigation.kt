package merail.tools.packages.impl

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object PackagesRoute

@Serializable
data class AppDetailsRoute(
    val packageName: String,
)

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

fun NavController.navigateToAppDetails(
    packageName: String,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = AppDetailsRoute(packageName),
        builder = {
            launchSingleTop = true
            navOptions()
        },
    )
}

fun NavGraphBuilder.packagesScreen(
    onAppClick: (String) -> Unit,
) {
    composable<PackagesRoute> {
        PackagesScreen(
            onAppClick = onAppClick,
        )
    }
}

fun NavGraphBuilder.appDetailsScreen() {
    composable<AppDetailsRoute> {
        AppDetailsScreen()
    }
}
