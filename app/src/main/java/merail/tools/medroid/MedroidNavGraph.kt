package merail.tools.medroid

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import merail.tools.info.impl.InfoRoute
import merail.tools.info.impl.infoScreen

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