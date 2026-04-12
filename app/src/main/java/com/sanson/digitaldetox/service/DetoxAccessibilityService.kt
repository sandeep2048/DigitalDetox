package com.sanson.digitaldetox.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.sanson.digitaldetox.data.db.AppDatabase
import com.sanson.digitaldetox.data.repository.AppRepository
import com.sanson.digitaldetox.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DetoxAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var appRepository: AppRepository

    private var currentForegroundPackage: String? = null
    private var overlayShowing = false

    // Which package the countdown timer is running for
    private var timerActiveForPackage: String? = null

    // Browser URL monitoring
    private var lastBrowserOverlayDomain: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        val db = AppDatabase.getInstance(applicationContext)
        appRepository = AppRepository(db.monitoredAppDao())

        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 300
        }

        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        // Ignore our own app
        if (packageName == applicationContext.packageName) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                handleWindowChange(packageName)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if (packageName in Constants.BROWSER_PACKAGES) {
                    handleBrowserContentChange(packageName)
                }
            }
        }
    }

    private fun handleWindowChange(packageName: String) {
        // Only act when the foreground PACKAGE actually changes
        if (packageName == currentForegroundPackage) return

        val previousPackage = currentForegroundPackage
        currentForegroundPackage = packageName

        // ── Step 1: Cancel timer if user left the monitored app ──
        if (timerActiveForPackage != null && packageName != timerActiveForPackage) {
            cancelTimer()
            timerActiveForPackage = null
        }

        // ── Step 2: Reset browser domain tracking on any package change ──
        lastBrowserOverlayDomain = null

        // ── Step 3: Don't show overlays for system packages ──
        if (isSystemPackage(packageName)) return

        // ── Step 4: For browsers, check URL ──
        if (packageName in Constants.BROWSER_PACKAGES) {
            handleBrowserContentChange(packageName)
            return
        }

        // ── Step 5: If overlay is already showing, don't stack another ──
        if (overlayShowing) return

        // ── Step 6: Check if this app is monitored and show overlay ──
        scope.launch {
            if (appRepository.isMonitored(packageName)) {
                showOverlay(packageName)
            }
        }
    }

    private fun isSystemPackage(packageName: String): Boolean {
        return packageName == "com.android.systemui" ||
                packageName.contains("launcher") ||
                packageName == "com.android.settings" ||
                packageName == "com.android.packageinstaller"
    }

    private fun cancelTimer() {
        try {
            val intent = Intent(applicationContext, OverlayService::class.java).apply {
                action = Constants.ACTION_CANCEL_TIMER
            }
            applicationContext.startService(intent)
        } catch (_: Exception) {}
    }

    private fun handleBrowserContentChange(browserPackage: String) {
        if (overlayShowing) return

        val rootNode = rootInActiveWindow ?: return
        val url = extractBrowserUrl(rootNode, browserPackage)
        rootNode.recycle()

        if (url.isNullOrBlank()) return

        val matchedDomain = Constants.MONITORED_DOMAINS.firstOrNull { domain ->
            url.contains(domain, ignoreCase = true)
        } ?: run {
            if (lastBrowserOverlayDomain != null) {
                lastBrowserOverlayDomain = null
            }
            return
        }

        if (matchedDomain == lastBrowserOverlayDomain) return

        lastBrowserOverlayDomain = matchedDomain

        val appName = Constants.DOMAIN_TO_APP_NAME[matchedDomain] ?: matchedDomain
        showOverlay("browser:$matchedDomain", appName)
    }

    private fun extractBrowserUrl(root: AccessibilityNodeInfo, browserPackage: String): String? {
        val urlBarIds = when (browserPackage) {
            "com.android.chrome" -> listOf(
                "com.android.chrome:id/url_bar",
                "com.android.chrome:id/search_box_text"
            )
            "org.mozilla.firefox" -> listOf(
                "org.mozilla.firefox:id/url_bar_title",
                "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"
            )
            "com.brave.browser" -> listOf(
                "com.brave.browser:id/url_bar",
                "com.brave.browser:id/search_box_text"
            )
            "com.microsoft.emmx" -> listOf(
                "com.microsoft.emmx:id/url_bar",
                "com.microsoft.emmx:id/search_box_text"
            )
            "com.sec.android.app.sbrowser" -> listOf(
                "com.sec.android.app.sbrowser:id/location_bar_edit_text"
            )
            else -> listOf("${browserPackage}:id/url_bar")
        }

        for (viewId in urlBarIds) {
            val nodes = root.findAccessibilityNodeInfosByViewId(viewId)
            if (nodes.isNullOrEmpty()) continue
            for (node in nodes) {
                val text = node.text?.toString()
                node.recycle()
                if (!text.isNullOrBlank()) return text
            }
        }

        return findUrlInTree(root)
    }

    private fun findUrlInTree(node: AccessibilityNodeInfo): String? {
        if (node.className?.toString() == "android.widget.EditText") {
            val text = node.text?.toString()
            if (text != null && (text.contains(".com") || text.contains(".org") || text.contains("http"))) {
                return text
            }
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findUrlInTree(child)
            child.recycle()
            if (result != null) return result
        }
        return null
    }

    private fun showOverlay(packageName: String, overrideAppName: String? = null) {
        overlayShowing = true
        val intent = Intent(applicationContext, OverlayService::class.java).apply {
            action = Constants.ACTION_SHOW_OVERLAY
            putExtra(Constants.EXTRA_PACKAGE_NAME, packageName)
            if (overrideAppName != null) {
                putExtra(Constants.EXTRA_APP_NAME, overrideAppName)
            }
        }
        applicationContext.startForegroundService(intent)
    }

    fun onOverlayShowing() {
        overlayShowing = true
    }

    fun onOverlayDismissed() {
        overlayShowing = false
    }

    fun onTimerStarted(packageName: String) {
        timerActiveForPackage = packageName
    }

    fun resetCurrentPackage() {
        currentForegroundPackage = null
        lastBrowserOverlayDomain = null
        timerActiveForPackage = null
    }

    fun goHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        applicationContext.startActivity(homeIntent)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        instance = null
    }

    companion object {
        var instance: DetoxAccessibilityService? = null
            private set
    }
}
