package com.sanson.digitaldetox

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.sanson.digitaldetox.data.db.AppDatabase
import com.sanson.digitaldetox.data.repository.MessageRepository
import com.sanson.digitaldetox.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DigitalDetoxApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        ensureDefaultMessages()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Keeps Digital Detox active in the background"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun ensureDefaultMessages() {
        appScope.launch {
            val db = AppDatabase.getInstance(this@DigitalDetoxApp)
            val messageRepo = MessageRepository(db.customMessageDao())
            messageRepo.ensureDefaultMessage()
        }
    }
}
