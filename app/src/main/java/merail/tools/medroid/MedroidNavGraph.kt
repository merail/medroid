package merail.tools.medroid

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import merail.tools.info.impl.InfoRoute
import merail.tools.info.impl.infoScreen
import merail.tools.packages.impl.PackagesRoute
import merail.tools.packages.impl.packagesScreen

@Composable
fun MedroidNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = PackagesRoute,
    ) {
        infoScreen()

        packagesScreen()
    }
}