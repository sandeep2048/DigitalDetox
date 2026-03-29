package com.sanson.digitaldetox.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_logs")
data class UsageLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val eventType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionDurationMs: Long? = null
) {
    companion object {
        const val EVENT_OPENED = "OPENED"
        const val EVENT_CONTINUED = "CONTINUED"
        const val EVENT_EXITED = "EXITED"
        const val EVENT_SESSION_NUDGE = "SESSION_NUDGE"
    }
}
