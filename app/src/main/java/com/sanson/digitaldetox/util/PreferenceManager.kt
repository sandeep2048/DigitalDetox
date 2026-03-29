package com.sanson.digitaldetox.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "digital_detox_prefs")

class PreferenceManager(private val context: Context) {

    companion object {
        val KEY_SERVICE_ENABLED = booleanPreferencesKey("service_enabled")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_COOLDOWN_SECONDS = intPreferencesKey("cooldown_seconds")
        val KEY_ESCALATION_ENABLED = booleanPreferencesKey("escalation_enabled")
        val KEY_LATE_NIGHT_MODE = booleanPreferencesKey("late_night_mode")
        val KEY_LATE_NIGHT_START = intPreferencesKey("late_night_start")
        val KEY_LATE_NIGHT_END = intPreferencesKey("late_night_end")
        val KEY_SESSION_NUDGE_ENABLED = booleanPreferencesKey("session_nudge_enabled")
        val KEY_SESSION_NUDGE_MINUTES = intPreferencesKey("session_nudge_minutes")
    }

    val isServiceEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SERVICE_ENABLED] ?: true }
    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { it[KEY_ONBOARDING_COMPLETED] ?: false }
    val cooldownSeconds: Flow<Int> = context.dataStore.data.map { it[KEY_COOLDOWN_SECONDS] ?: Constants.DEFAULT_COOLDOWN_SECONDS }
    val isEscalationEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_ESCALATION_ENABLED] ?: true }
    val isLateNightModeEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_LATE_NIGHT_MODE] ?: true }
    val lateNightStart: Flow<Int> = context.dataStore.data.map { it[KEY_LATE_NIGHT_START] ?: Constants.DEFAULT_LATE_NIGHT_START }
    val lateNightEnd: Flow<Int> = context.dataStore.data.map { it[KEY_LATE_NIGHT_END] ?: Constants.DEFAULT_LATE_NIGHT_END }
    val isSessionNudgeEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SESSION_NUDGE_ENABLED] ?: true }
    val sessionNudgeMinutes: Flow<Int> = context.dataStore.data.map { it[KEY_SESSION_NUDGE_MINUTES] ?: Constants.SESSION_NUDGE_MINUTES }

    suspend fun setServiceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SERVICE_ENABLED] = enabled }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setCooldownSeconds(seconds: Int) {
        context.dataStore.edit { it[KEY_COOLDOWN_SECONDS] = seconds }
    }

    suspend fun setEscalationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_ESCALATION_ENABLED] = enabled }
    }

    suspend fun setLateNightMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_LATE_NIGHT_MODE] = enabled }
    }

    suspend fun setLateNightStart(hour: Int) {
        context.dataStore.edit { it[KEY_LATE_NIGHT_START] = hour }
    }

    suspend fun setLateNightEnd(hour: Int) {
        context.dataStore.edit { it[KEY_LATE_NIGHT_END] = hour }
    }

    suspend fun setSessionNudgeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SESSION_NUDGE_ENABLED] = enabled }
    }

    suspend fun setSessionNudgeMinutes(minutes: Int) {
        context.dataStore.edit { it[KEY_SESSION_NUDGE_MINUTES] = minutes }
    }

    suspend fun getCooldownForOpens(opensToday: Int): Int {
        val prefs = context.dataStore.data.first()
        val baseCooldown = prefs[KEY_COOLDOWN_SECONDS] ?: Constants.DEFAULT_COOLDOWN_SECONDS
        val escalation = prefs[KEY_ESCALATION_ENABLED] ?: true
        val lateNight = prefs[KEY_LATE_NIGHT_MODE] ?: true

        var cooldown = if (escalation) {
            when {
                opensToday >= Constants.ESCALATION_TIER_3_OPENS -> Constants.ESCALATION_TIER_3_SECONDS
                opensToday >= Constants.ESCALATION_TIER_2_OPENS -> Constants.ESCALATION_TIER_2_SECONDS
                else -> baseCooldown
            }
        } else {
            baseCooldown
        }

        if (lateNight && TimeUtils.isLateNight(
                prefs[KEY_LATE_NIGHT_START] ?: Constants.DEFAULT_LATE_NIGHT_START,
                prefs[KEY_LATE_NIGHT_END] ?: Constants.DEFAULT_LATE_NIGHT_END
            )) {
            cooldown *= Constants.LATE_NIGHT_MULTIPLIER
        }

        return cooldown
    }
}
