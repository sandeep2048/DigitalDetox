package com.sanson.digitaldetox.data.repository

import com.sanson.digitaldetox.data.db.dao.CustomMessageDao
import com.sanson.digitaldetox.data.db.entity.CustomMessageEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MessageRepository(private val dao: CustomMessageDao) {

    fun getAllMessages(): Flow<List<CustomMessageEntity>> = dao.getAllMessages()

    suspend fun getActiveMessages(): List<CustomMessageEntity> = dao.getActiveMessages()

    suspend fun getMessageForCurrentTime(): CustomMessageEntity? {
        val messages = dao.getActiveMessages()
        if (messages.isEmpty()) return null

        val now = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val timeSpecific = messages.firstOrNull { msg ->
            if (msg.timeRangeStart != null && msg.timeRangeEnd != null) {
                val start = LocalTime.parse(msg.timeRangeStart, formatter)
                val end = LocalTime.parse(msg.timeRangeEnd, formatter)
                if (start.isBefore(end)) {
                    now.isAfter(start) && now.isBefore(end)
                } else {
                    now.isAfter(start) || now.isBefore(end)
                }
            } else {
                false
            }
        }

        return timeSpecific ?: messages.random()
    }

    suspend fun addMessage(
        message: String,
        timeRangeStart: String? = null,
        timeRangeEnd: String? = null
    ): Long {
        return dao.insert(
            CustomMessageEntity(
                message = message,
                timeRangeStart = timeRangeStart,
                timeRangeEnd = timeRangeEnd
            )
        )
    }

    suspend fun updateMessage(entity: CustomMessageEntity) {
        dao.update(entity)
    }

    suspend fun deleteMessage(id: Long) {
        dao.deleteById(id)
    }

    suspend fun getMessageCount(): Int = dao.getCount()

    suspend fun ensureDefaultMessage() {
        if (dao.getCount() == 0) {
            dao.insert(
                CustomMessageEntity(
                    message = "Is this really how you want to spend your time right now?"
                )
            )
            dao.insert(
                CustomMessageEntity(
                    message = "Remember your goals. Every minute here is a minute lost.",
                    timeRangeStart = "22:00",
                    timeRangeEnd = "06:00"
                )
            )
        }
    }
}
