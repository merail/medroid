package merail.tools.medroid

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import merail.tools.info.InfoRoute
import merail.tools.info.infoScreen

@Composable
fun MedroidNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = InfoRoute,
    ) {
        infoScreen()
    }
}