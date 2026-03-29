package com.sanson.digitaldetox.data.repository

import com.sanson.digitaldetox.data.db.dao.MonitoredAppDao
import com.sanson.digitaldetox.data.db.entity.MonitoredAppEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: MonitoredAppDao) {

    fun getAllApps(): Flow<List<MonitoredAppEntity>> = dao.getAllApps()

    fun getEnabledApps(): Flow<List<MonitoredAppEntity>> = dao.getEnabledApps()

    suspend fun getEnabledPackageNames(): List<String> = dao.getEnabledPackageNames()

    suspend fun isMonitored(packageName: String): Boolean = dao.isMonitored(packageName)

    suspend fun addApp(packageName: String, appName: String) {
        dao.insert(MonitoredAppEntity(packageName = packageName, appName = appName))
    }

    suspend fun removeApp(packageName: String) {
        dao.deleteByPackage(packageName)
    }

    suspend fun setEnabled(packageName: String, enabled: Boolean) {
        dao.setEnabled(packageName, enabled)
    }

    suspend fun addApps(apps: List<MonitoredAppEntity>) {
        dao.insertAll(apps)
    }
}
