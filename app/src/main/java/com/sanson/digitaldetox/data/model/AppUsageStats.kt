package com.sanson.digitaldetox.data.model

data class AppUsageStats(
    val packageName: String,
    val appName: String,
    val opensToday: Int,
    val timeSpentTodayMs: Long,
    val timeSpentWeekMs: Long,
    val mindfulExitsToday: Int
)

data class DailyStats(
    val dayTimestamp: Long,
    val totalDurationMs: Long,
    val totalOpens: Int
)

data class OverlayData(
    val customMessage: String,
    val opensToday: Int,
    val timeSpentTodayMs: Long,
    val timeSpentWeekMs: Long,
    val cooldownSeconds: Int,
    val packageName: String,
    val appName: String,
    val nudgeAfterMinutes: Int = 5,
    val intentCountsToday: Map<String, Int> = emptyMap()
)
