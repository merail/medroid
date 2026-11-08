package merail.tools.medroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import merail.tools.home.HomeRoute
import merail.tools.home.homeScreen
import merail.tools.info.impl.infoScreen
import merail.tools.info.impl.navigateToInfo
import merail.tools.packages.impl.navigateToPackages
import merail.tools.packages.impl.packagesScreen

@Composable
fun MedroidNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        homeScreen(
            navigateToInfo = navController::navigateToInfo,
            navigateToPackages = navController::navigateToPackages,
        )

        infoScreen()

        packagesScreen()
    }
}
