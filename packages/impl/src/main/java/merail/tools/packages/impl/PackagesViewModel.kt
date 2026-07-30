package merail.tools.packages.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import merail.tools.packages.api.IPackagesRepository
import javax.inject.Inject

@HiltViewModel
internal class PackagesViewModel @Inject constructor(
    packagesRepository: IPackagesRepository,
) : ViewModel() {

    private val allApps = packagesRepository.getInstalledApps()

    val searchedApps = MutableStateFlow(allApps)

    fun search(query: String) {
        searchedApps.value = if (query.isBlank()) {
            allApps
        } else {
            val q = query.lowercase()
            allApps.filter { app ->
                val name = app.appName.lowercase()
                val pkg = app.packageName.lowercase()

                Log.d("AAAAAAAAAAAA", (similarity(name, q)).toString())

                name.contains(q) || pkg.contains(q)
                        || similarity(name, q) >= 0.7
                        || similarity(pkg, q) >= 0.7
            }
        }
    }


    private fun similarity(a: String, b: String): Double {
        val distance = levenshtein(a, b)
        return 1.0 - distance.toDouble() / maxOf(a.length, b.length)
    }

    private fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[a.length][b.length]
    }
}
