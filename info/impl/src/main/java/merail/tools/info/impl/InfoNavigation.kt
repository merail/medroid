package merail.tools.info.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object InfoRoute

fun NavGraphBuilder.infoScreen() {
    composable<InfoRoute> {
        InfoScreen()
    }
}