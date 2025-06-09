package app.solution.dailyup.utility

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object LocalDataManager {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(ConstKeys.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    fun put(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun get(key: String): String? {
        return prefs.getString(key, null)
    }
}