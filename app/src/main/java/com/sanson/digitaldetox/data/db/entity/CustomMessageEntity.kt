package com.sanson.digitaldetox.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_messages")
data class CustomMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val message: String,
    val isActive: Boolean = true,
    val timeRangeStart: String? = null,
    val timeRangeEnd: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
