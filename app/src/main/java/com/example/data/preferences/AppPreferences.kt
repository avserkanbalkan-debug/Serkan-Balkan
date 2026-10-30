package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("lgs_master_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_API_KEY = "lgs_gemini_key"
        private const val KEY_NET_PENALTY = "lgs_net_penalty" // 3.0 (MEB) or 4.0
        private const val KEY_DEFAULT_COUNT = "lgs_default_count" // 5, 10, 20
    }

    var apiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value.trim()).apply()

    var netPenalty: Float
        get() = prefs.getFloat(KEY_NET_PENALTY, 3.0f)
        set(value) = prefs.edit().putFloat(KEY_NET_PENALTY, value).apply()

    var defaultCount: Int
        get() = prefs.getInt(KEY_DEFAULT_COUNT, 10)
        set(value) = prefs.edit().putInt(KEY_DEFAULT_COUNT, value).apply()
}
