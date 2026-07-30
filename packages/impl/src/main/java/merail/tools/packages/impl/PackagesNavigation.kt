package merail.tools.packages.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object PackagesRoute

fun NavGraphBuilder.packagesScreen() {
    composable<PackagesRoute> {
        PackagesScreen()
    }
}