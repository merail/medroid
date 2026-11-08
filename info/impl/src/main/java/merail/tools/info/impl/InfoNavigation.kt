package merail.tools.info.impl

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object InfoRoute

fun NavController.navigateToInfo(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = InfoRoute,
        builder = {
            launchSingleTop = true
            navOptions()
        },
    )
}

fun NavGraphBuilder.infoScreen() {
    composable<InfoRoute> {
        InfoScreen()
    }
}