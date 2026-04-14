package com.sanson.digitaldetox.data.repository

import com.sanson.digitaldetox.data.db.dao.UsageLogDao
import com.sanson.digitaldetox.data.db.entity.UsageLogEntity
import com.sanson.digitaldetox.data.model.AppUsageStats
import com.sanson.digitaldetox.data.model.DailyStats
import com.sanson.digitaldetox.util.TimeUtils

class UsageRepository(
    private val usageLogDao: UsageLogDao,
    private val appRepository: AppRepository
) {

    suspend fun logEvent(packageName: String, eventType: String, sessionDurationMs: Long? = null) {
        usageLogDao.insert(
            UsageLogEntity(
                packageName = packageName,
                eventType = eventType,
                sessionDurationMs = sessionDurationMs
            )
        )
    }

    suspend fun getOpensToday(packageName: String): Int {
        return usageLogDao.getOpenCountSince(packageName, TimeUtils.startOfToday())
    }

    suspend fun getTotalOpensToday(): Int {
        return usageLogDao.getTotalOpenCountSince(TimeUtils.startOfToday())
    }

    suspend fun getMindfulExitsToday(): Int {
        return usageLogDao.getMindfulExitsSince(TimeUtils.startOfToday())
    }

    suspend fun getTimeSpentToday(packageName: String): Long {
        return usageLogDao.getTotalDurationSince(packageName, TimeUtils.startOfToday())
    }

    suspend fun getTotalTimeSpentToday(): Long {
        return usageLogDao.getTotalDurationAllAppsSince(TimeUtils.startOfToday())
    }

    suspend fun getTimeSpentThisWeek(packageName: String): Long {
        return usageLogDao.getTotalDurationSince(packageName, TimeUtils.startOfWeek())
    }

    suspend fun getIntentCountsToday(packageName: String): Map<String, Int> {
        val rows = usageLogDao.getContinuedIntentCountsSince(packageName, TimeUtils.startOfToday())
        return rows.mapNotNull { row ->
            UsageLogEntity.intentKeyFromEvent(row.eventType)?.let { key -> key to row.count }
        }.toMap()
    }

    suspend fun getTotalTimeSpentThisWeek(): Long {
        return usageLogDao.getTotalDurationAllAppsSince(TimeUtils.startOfWeek())
    }

    suspend fun getPerAppStats(): List<AppUsageStats> {
        val today = TimeUtils.startOfToday()
        val weekStart = TimeUtils.startOfWeek()
        val packages = usageLogDao.getOpenedPackagesSince(weekStart)

        return packages.map { pkg ->
            val appName = try {
                appRepository.getAllApps().let { pkg }
            } catch (_: Exception) {
                pkg
            }
            AppUsageStats(
                packageName = pkg,
                appName = appName,
                opensToday = usageLogDao.getOpenCountSince(pkg, today),
                timeSpentTodayMs = usageLogDao.getTotalDurationSince(pkg, today),
                timeSpentWeekMs = usageLogDao.getTotalDurationSince(pkg, weekStart),
                mindfulExitsToday = 0
            )
        }
    }

    suspend fun getWeeklyDailyStats(): List<DailyStats> {
        val days = TimeUtils.getLast7Days()
        return days.map { (start, end) ->
            DailyStats(
                dayTimestamp = start,
                totalDurationMs = usageLogDao.getTotalDurationForDay(start, end),
                totalOpens = 0
            )
        }
    }

    suspend fun cleanupOldData() {
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        usageLogDao.deleteOlderThan(thirtyDaysAgo)
    }
}
