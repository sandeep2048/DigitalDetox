package com.sanson.digitaldetox.ui.screens.apps

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanson.digitaldetox.data.db.AppDatabase
import com.sanson.digitaldetox.data.db.entity.MonitoredAppEntity
import com.sanson.digitaldetox.data.repository.AppRepository
import com.sanson.digitaldetox.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val isMonitored: Boolean
)

class AppSelectorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val appRepo = AppRepository(db.monitoredAppDao())

    val monitoredApps = appRepo.getAllApps()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val pm = getApplication<Application>().packageManager
            val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { it.packageName != getApplication<Application>().packageName }
                .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
                .map { appInfo ->
                    InstalledApp(
                        packageName = appInfo.packageName,
                        appName = pm.getApplicationLabel(appInfo).toString(),
                        isMonitored = false
                    )
                }
                .sortedBy { it.appName.lowercase() }

            _installedApps.value = apps
        }
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun toggleApp(packageName: String, appName: String, enable: Boolean) {
        viewModelScope.launch {
            if (enable) {
                appRepo.addApp(packageName, appName)
            } else {
                appRepo.removeApp(packageName)
            }
        }
    }

    fun getSuggestedApps(): List<InstalledApp> {
        val installed = _installedApps.value.map { it.packageName }.toSet()
        return Constants.DEFAULT_SOCIAL_APPS
            .filter { it.key in installed }
            .map { (pkg, name) ->
                InstalledApp(
                    packageName = pkg,
                    appName = name,
                    isMonitored = false
                )
            }
    }
}
