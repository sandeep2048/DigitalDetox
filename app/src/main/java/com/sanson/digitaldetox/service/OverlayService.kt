package com.sanson.digitaldetox.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sanson.digitaldetox.MainActivity
import com.sanson.digitaldetox.R
import com.sanson.digitaldetox.data.db.AppDatabase
import com.sanson.digitaldetox.data.db.entity.UsageLogEntity
import com.sanson.digitaldetox.data.model.OverlayData
import com.sanson.digitaldetox.data.repository.AppRepository
import com.sanson.digitaldetox.data.repository.MessageRepository
import com.sanson.digitaldetox.data.repository.UsageRepository
import com.sanson.digitaldetox.ui.overlay.CountdownTimerOverlay
import com.sanson.digitaldetox.ui.overlay.InterventionOverlay
import com.sanson.digitaldetox.ui.theme.DigitalDetoxTheme
import com.sanson.digitaldetox.util.Constants
import com.sanson.digitaldetox.util.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OverlayService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var windowManager: WindowManager? = null

    // Full-screen intervention overlay
    private var overlayView: ComposeView? = null
    private var overlayLifecycle: OverlayLifecycleOwner = OverlayLifecycleOwner()

    // Floating countdown timer
    private var timerView: ComposeView? = null
    private var timerLifecycle: OverlayLifecycleOwner = OverlayLifecycleOwner()

    private var currentPackage: String? = null
    private var currentAppNameOverride: String? = null
    private var sessionStartTime: Long = 0

    private lateinit var usageRepository: UsageRepository
    private lateinit var messageRepository: MessageRepository
    private lateinit var appRepository: AppRepository
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(applicationContext)
        appRepository = AppRepository(db.monitoredAppDao())
        usageRepository = UsageRepository(db.usageLogDao(), appRepository)
        messageRepository = MessageRepository(db.customMessageDao())
        preferenceManager = PreferenceManager(applicationContext)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTION_SHOW_OVERLAY -> {
                val packageName = intent.getStringExtra(Constants.EXTRA_PACKAGE_NAME)
                val appNameOverride = intent.getStringExtra(Constants.EXTRA_APP_NAME)
                if (packageName != null) {
                    logSessionDurationIfNeeded()
                    removeTimerView()
                    currentAppNameOverride = appNameOverride
                    scope.launch { prepareAndShowOverlay(packageName, appNameOverride) }
                }
            }
            Constants.ACTION_DISMISS_OVERLAY -> {
                removeOverlayView()
                removeTimerView()
            }
            Constants.ACTION_CANCEL_TIMER -> {
                // User left the monitored app — kill the floating timer
                removeTimerView()
                logSessionDurationIfNeeded()
            }
        }
        return START_STICKY
    }

    // ── Full Intervention Overlay ──

    private suspend fun prepareAndShowOverlay(packageName: String, appNameOverride: String? = null) {
        currentPackage = packageName

        usageRepository.logEvent(packageName, UsageLogEntity.EVENT_OPENED)

        val opensToday = usageRepository.getOpensToday(packageName)
        val timeToday = usageRepository.getTimeSpentToday(packageName)
        val timeWeek = usageRepository.getTimeSpentThisWeek(packageName)
        val cooldown = preferenceManager.getCooldownForOpens(opensToday)

        val message = messageRepository.getMessageForCurrentTime()
        val messageText = message?.message ?: "Do you really need to be here right now?"

        val appName = appNameOverride ?: try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(packageName, 0)
            ).toString()
        } catch (_: Exception) {
            packageName.substringAfterLast(".")
        }

        val nudgeMinutes = preferenceManager.sessionNudgeMinutes.first()

        val data = OverlayData(
            customMessage = messageText,
            opensToday = opensToday,
            timeSpentTodayMs = timeToday,
            timeSpentWeekMs = timeWeek,
            cooldownSeconds = cooldown,
            packageName = packageName,
            appName = appName,
            nudgeAfterMinutes = nudgeMinutes
        )

        showFullOverlay(data)
    }

    private fun showFullOverlay(data: OverlayData) {
        removeOverlayView()
        removeTimerView()

        DetoxAccessibilityService.instance?.onOverlayShowing()

        overlayLifecycle = OverlayLifecycleOwner()
        overlayLifecycle.onCreate()
        overlayLifecycle.onResume()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        )

        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(overlayLifecycle)
            setViewTreeSavedStateRegistryOwner(overlayLifecycle)
            setContent {
                DigitalDetoxTheme(darkTheme = true) {
                    InterventionOverlay(
                        data = data,
                        onContinue = { handleContinue(data.nudgeAfterMinutes) },
                        onGoBack = { handleGoBack() }
                    )
                }
            }
        }

        windowManager?.addView(overlayView, params)
    }

    private fun handleContinue(nudgeMinutes: Int) {
        val pkg = currentPackage ?: return
        scope.launch {
            sessionStartTime = System.currentTimeMillis()
            usageRepository.logEvent(pkg, UsageLogEntity.EVENT_CONTINUED)
            DetoxAccessibilityService.instance?.onOverlayDismissed()
            DetoxAccessibilityService.instance?.onTimerStarted(pkg)
            removeOverlayView()
            showTimerView(nudgeMinutes)
        }
    }

    private fun handleGoBack() {
        val pkg = currentPackage ?: return
        scope.launch {
            usageRepository.logEvent(pkg, UsageLogEntity.EVENT_EXITED)
            DetoxAccessibilityService.instance?.onOverlayDismissed()
            DetoxAccessibilityService.instance?.resetCurrentPackage()
            removeOverlayView()
            removeTimerView()
            sessionStartTime = 0
            DetoxAccessibilityService.instance?.goHome()
        }
    }

    // ── Floating Countdown Timer ──

    private fun showTimerView(nudgeMinutes: Int) {
        removeTimerView()

        timerLifecycle = OverlayLifecycleOwner()
        timerLifecycle.onCreate()
        timerLifecycle.onResume()

        val totalSeconds = nudgeMinutes * 60

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 24
            y = 100
        }

        timerView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(timerLifecycle)
            setViewTreeSavedStateRegistryOwner(timerLifecycle)
            setContent {
                DigitalDetoxTheme(darkTheme = true) {
                    CountdownTimerOverlay(
                        totalSeconds = totalSeconds,
                        onTimerFinished = {
                            scope.launch { onNudgeTimerExpired() }
                        }
                    )
                }
            }
        }

        windowManager?.addView(timerView, params)
    }

    private fun onNudgeTimerExpired() {
        val pkg = currentPackage ?: return
        removeTimerView()
        scope.launch {
            val elapsed = System.currentTimeMillis() - sessionStartTime
            usageRepository.logEvent(pkg, UsageLogEntity.EVENT_SESSION_NUDGE, elapsed)
            sessionStartTime = 0
            prepareAndShowOverlay(pkg, currentAppNameOverride)
        }
    }

    // ── View Cleanup ──

    private fun removeOverlayView() {
        try {
            overlayView?.let { windowManager?.removeView(it) }
        } catch (_: Exception) {}
        overlayView = null
        overlayLifecycle.onDestroy()
    }

    private fun removeTimerView() {
        try {
            timerView?.let { windowManager?.removeView(it) }
        } catch (_: Exception) {}
        timerView = null
        timerLifecycle.onDestroy()
    }

    private fun logSessionDurationIfNeeded() {
        if (sessionStartTime > 0 && currentPackage != null) {
            val duration = System.currentTimeMillis() - sessionStartTime
            if (duration > 1000) {
                scope.launch {
                    usageRepository.logEvent(
                        currentPackage!!,
                        UsageLogEntity.EVENT_CONTINUED,
                        duration
                    )
                }
            }
            sessionStartTime = 0
        }
    }

    // ── Notification ──

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

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Digital Detox Active")
            .setContentText("Protecting you from mindless scrolling")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        logSessionDurationIfNeeded()
        removeOverlayView()
        removeTimerView()
        scope.cancel()
        super.onDestroy()
    }
}

class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private var isCreated = false

    override val lifecycle: Lifecycle = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry = savedStateRegistryController.savedStateRegistry

    fun onCreate() {
        if (!isCreated) {
            savedStateRegistryController.performRestore(null)
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        isCreated = true
    }

    fun onResume() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun onDestroy() {
        if (isCreated) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            isCreated = false
        }
    }
}
