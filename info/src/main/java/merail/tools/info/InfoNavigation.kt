package merail.tools.info

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object InfoRoute

fun NavController.navigateToContent(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = InfoRoute,
        builder = navOptions,
    )
}

fun NavGraphBuilder.infoScreen() {
    composable<InfoRoute> {
        InfoScreen()
    }
}