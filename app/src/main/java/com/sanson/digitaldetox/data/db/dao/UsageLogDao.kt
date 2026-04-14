package com.sanson.digitaldetox.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sanson.digitaldetox.data.db.entity.UsageLogEntity
import kotlinx.coroutines.flow.Flow

data class EventTypeCount(
    val eventType: String,
    val count: Int
)

@Dao
interface UsageLogDao {

    @Insert
    suspend fun insert(log: UsageLogEntity)

    @Query("""
        SELECT COUNT(*) FROM usage_logs
        WHERE packageName = :packageName
        AND eventType = 'OPENED'
        AND timestamp >= :since
    """)
    suspend fun getOpenCountSince(packageName: String, since: Long): Int

    @Query("""
        SELECT COUNT(*) FROM usage_logs
        WHERE eventType = 'OPENED'
        AND timestamp >= :since
    """)
    suspend fun getTotalOpenCountSince(since: Long): Int

    @Query("""
        SELECT COUNT(*) FROM usage_logs
        WHERE eventType = 'EXITED'
        AND timestamp >= :since
    """)
    suspend fun getMindfulExitsSince(since: Long): Int

    @Query("""
        SELECT COALESCE(SUM(sessionDurationMs), 0) FROM usage_logs
        WHERE packageName = :packageName
        AND timestamp >= :since
        AND sessionDurationMs IS NOT NULL
    """)
    suspend fun getTotalDurationSince(packageName: String, since: Long): Long

    @Query("""
        SELECT COALESCE(SUM(sessionDurationMs), 0) FROM usage_logs
        WHERE timestamp >= :since
        AND sessionDurationMs IS NOT NULL
    """)
    suspend fun getTotalDurationAllAppsSince(since: Long): Long

    @Query("""
        SELECT DISTINCT packageName FROM usage_logs
        WHERE eventType = 'OPENED'
        AND timestamp >= :since
    """)
    suspend fun getOpenedPackagesSince(since: Long): List<String>

    @Query("""
        SELECT eventType, COUNT(*) AS count FROM usage_logs
        WHERE packageName = :packageName
        AND timestamp >= :since
        AND eventType LIKE 'CONTINUED:%'
        GROUP BY eventType
    """)
    suspend fun getContinuedIntentCountsSince(packageName: String, since: Long): List<EventTypeCount>

    @Query("""
        SELECT COUNT(*) FROM usage_logs
        WHERE packageName = :packageName
        AND eventType = 'OPENED'
        AND timestamp >= :dayStart
        AND timestamp < :dayEnd
    """)
    suspend fun getOpenCountForDay(packageName: String, dayStart: Long, dayEnd: Long): Int

    @Query("""
        SELECT COALESCE(SUM(sessionDurationMs), 0) FROM usage_logs
        WHERE timestamp >= :dayStart
        AND timestamp < :dayEnd
        AND sessionDurationMs IS NOT NULL
    """)
    suspend fun getTotalDurationForDay(dayStart: Long, dayEnd: Long): Long

    @Query("""
        SELECT * FROM usage_logs
        WHERE packageName = :packageName
        ORDER BY timestamp DESC
        LIMIT :limit
    """)
    fun getRecentLogs(packageName: String, limit: Int = 50): Flow<List<UsageLogEntity>>

    @Query("DELETE FROM usage_logs WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)
}
