package merail.tools.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    navigateToInfo: () -> Unit,
    navigateToPackages: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onInfoClick = navigateToInfo,
            onPackagesClick = navigateToPackages,
        )
    }
}
