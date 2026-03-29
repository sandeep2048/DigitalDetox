package com.sanson.digitaldetox.util

object Constants {
    const val DEFAULT_COOLDOWN_SECONDS = 8
    const val ESCALATION_TIER_2_OPENS = 4
    const val ESCALATION_TIER_2_SECONDS = 15
    const val ESCALATION_TIER_3_OPENS = 8
    const val ESCALATION_TIER_3_SECONDS = 30
    const val LATE_NIGHT_MULTIPLIER = 2
    const val DEFAULT_LATE_NIGHT_START = 22
    const val DEFAULT_LATE_NIGHT_END = 6
    const val SESSION_NUDGE_MINUTES = 5
    const val OVERLAY_BYPASS_COOLDOWN_MS = 2000L

    const val NOTIFICATION_CHANNEL_ID = "digital_detox_service"
    const val NOTIFICATION_CHANNEL_NAME = "Digital Detox Protection"
    const val FOREGROUND_NOTIFICATION_ID = 1001
    const val SESSION_NUDGE_NOTIFICATION_ID = 1002

    const val ACTION_SHOW_OVERLAY = "com.sanson.digitaldetox.SHOW_OVERLAY"
    const val ACTION_DISMISS_OVERLAY = "com.sanson.digitaldetox.DISMISS_OVERLAY"
    const val EXTRA_PACKAGE_NAME = "extra_package_name"
    const val EXTRA_APP_NAME = "extra_app_name"

    // ── Social Media Blocks ──
    // Each entry: display name, package name, icon emoji, associated browser domains
    data class SocialBlock(
        val name: String,
        val packageName: String,
        val emoji: String,
        val domains: List<String>,
        val description: String
    )

    val SOCIAL_MEDIA_BLOCKS = listOf(
        SocialBlock(
            name = "YouTube",
            packageName = "com.google.android.youtube",
            emoji = "YT",
            domains = listOf("youtube.com", "youtu.be", "m.youtube.com"),
            description = "Videos & Shorts"
        ),
        SocialBlock(
            name = "YouTube Shorts",
            packageName = "com.google.android.youtube",
            emoji = "YS",
            domains = listOf("youtube.com/shorts"),
            description = "Short-form videos"
        ),
        SocialBlock(
            name = "Instagram",
            packageName = "com.instagram.android",
            emoji = "IG",
            domains = listOf("instagram.com", "www.instagram.com"),
            description = "Reels, Stories & Feed"
        ),
        SocialBlock(
            name = "X (Twitter)",
            packageName = "com.twitter.android",
            emoji = "X",
            domains = listOf("twitter.com", "x.com", "mobile.twitter.com"),
            description = "Tweets & Spaces"
        ),
        SocialBlock(
            name = "LinkedIn",
            packageName = "com.linkedin.android",
            emoji = "LI",
            domains = listOf("linkedin.com", "www.linkedin.com"),
            description = "Feed & Messaging"
        )
    )

    val DEFAULT_SOCIAL_APPS = mapOf(
        "com.google.android.youtube" to "YouTube",
        "com.instagram.android" to "Instagram",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.snapchat.android" to "Snapchat",
        "com.twitter.android" to "X (Twitter)",
        "com.facebook.katana" to "Facebook",
        "com.reddit.frontpage" to "Reddit",
        "com.linkedin.android" to "LinkedIn",
        "com.pinterest" to "Pinterest"
    )

    val BROWSER_PACKAGES = setOf(
        "com.android.chrome",
        "org.mozilla.firefox",
        "com.brave.browser",
        "com.opera.browser",
        "com.microsoft.emmx",
        "com.sec.android.app.sbrowser",
        "com.UCMobile.intl"
    )

    // All monitored domains — built dynamically from SOCIAL_MEDIA_BLOCKS
    val MONITORED_DOMAINS: List<String>
        get() = SOCIAL_MEDIA_BLOCKS.flatMap { it.domains }.distinct()

    val DOMAIN_TO_APP_NAME: Map<String, String>
        get() = SOCIAL_MEDIA_BLOCKS.flatMap { block ->
            block.domains.map { domain -> domain to "${block.name} (Browser)" }
        }.toMap()
}
