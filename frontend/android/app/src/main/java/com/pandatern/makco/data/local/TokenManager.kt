package com.pandatern.makco.data.local

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "makco_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        prefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        return prefs(context).getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        prefs(context).edit().remove(KEY_TOKEN).apply()
    }

    fun hasToken(context: Context): Boolean {
        return getToken(context)?.isNotEmpty() == true
    }

    fun setOnboardingDone(context: Context) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
    }

    fun isOnboardingDone(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_ONBOARDING_DONE, false)
    }
}
