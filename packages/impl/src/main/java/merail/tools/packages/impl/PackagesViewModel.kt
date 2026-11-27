package merail.tools.packages.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import merail.tools.packages.api.IPackagesRepository
import merail.tools.packages.api.model.InstalledAppInfo
import javax.inject.Inject

@HiltViewModel
internal class PackagesViewModel @Inject constructor(
    private val packagesRepository: IPackagesRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _allApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())

    private val _searchedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val searchedApps: StateFlow<List<InstalledAppInfo>> = _searchedApps.asStateFlow()

    private val _selectedFilter = MutableStateFlow(AppFilter.ALL)
    val selectedFilter: StateFlow<AppFilter> = _selectedFilter.asStateFlow()

    private var currentQuery = ""
    private var currentFilter = AppFilter.ALL

    private val loadedIcons = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                packagesRepository.getInstalledApps()
            }
            _allApps.value = apps
            _searchedApps.value = filter()
            _isLoading.value = false
        }
    }

    fun search(query: String) {
        currentQuery = query
        _searchedApps.value = filter()
    }

    fun setFilter(filter: AppFilter) {
        currentFilter = filter
        _selectedFilter.value = filter
        _searchedApps.value = filter()
    }

    fun loadIcons(packageNames: List<String>) {
        val toLoad = packageNames
            .filter { it !in loadedIcons }
            .distinct()

        if (toLoad.isEmpty()) return

        loadedIcons += toLoad

        viewModelScope.launch {
            val icons = withContext(Dispatchers.IO) {
                toLoad.associateWith { packagesRepository.getAppIcon(it) }
            }

            val updated = _allApps.value.map { app ->
                icons[app.packageName]?.let { icon -> app.copy(icon = icon) } ?: app
            }
            _allApps.value = updated
            _searchedApps.value = filter()
        }
    }

    private fun filter(): List<InstalledAppInfo> {
        val filtered = when (currentFilter) {
            AppFilter.ALL -> _allApps.value
            AppFilter.USER -> _allApps.value.filter { !it.isSystemApp }
            AppFilter.SYSTEM -> _allApps.value.filter { it.isSystemApp }
            AppFilter.DEBUGGABLE -> _allApps.value.filter { it.debuggable }
        }

        if (currentQuery.isBlank()) {
            return filtered
        }

        val q = currentQuery.lowercase()
        return filtered.filter { app ->
            val name = app.appName.lowercase()
            val pkg = app.packageName.lowercase()

            name.contains(q) || pkg.contains(q)
                    || similarity(name, q) >= 0.7
                    || similarity(pkg, q) >= 0.7
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
                    dp[i - 1][j - 1] + cost,
                )
            }
        }
        return dp[a.length][b.length]
    }
}
