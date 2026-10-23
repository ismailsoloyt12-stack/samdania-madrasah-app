package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences

object SupabaseConfig {
    private const val PREF_NAME = "supabase_madrasah_prefs"
    private const val KEY_URL = "supabase_url"
    private const val KEY_ANON_KEY = "supabase_anon_key"

    // Live production Supabase instance for Samdania Madrasah Management System
    const val DEFAULT_SUPABASE_URL = "https://ursocwwzhkzdpjptpvkl.supabase.co"
    const val DEFAULT_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVyc29jd3d6aGt6ZHBqcHRwdmtsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODQwNDczOTQsImV4cCI6MjA5OTYyMzM5NH0.AeKjm9FdfLC0mI38X-ydoeXBLQGpQ-Gs7ZPk2-EBfjs"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getProjectUrl(context: Context): String {
        val saved = getPrefs(context).getString(KEY_URL, null)
        val url = if (saved.isNullOrBlank() || saved.contains("kswrqyvujzpqmfvxljtu")) {
            // Automatically migrate to live production database
            getPrefs(context).edit().putString(KEY_URL, DEFAULT_SUPABASE_URL).apply()
            DEFAULT_SUPABASE_URL
        } else {
            saved
        }
        return if (url.endsWith("/")) url else "$url/"
    }

    fun getAnonKey(context: Context): String {
        val saved = getPrefs(context).getString(KEY_ANON_KEY, null)
        return if (saved.isNullOrBlank() || saved.contains("sample_public_anon_key") || saved.contains("kswrqyvujzpqmfvxljtu")) {
            getPrefs(context).edit().putString(KEY_ANON_KEY, DEFAULT_ANON_KEY).apply()
            DEFAULT_ANON_KEY
        } else {
            saved
        }
    }

    fun saveConfig(context: Context, url: String, anonKey: String) {
        getPrefs(context).edit()
            .putString(KEY_URL, url.trim())
            .putString(KEY_ANON_KEY, anonKey.trim())
            .apply()
    }

    fun resetToDefault(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
