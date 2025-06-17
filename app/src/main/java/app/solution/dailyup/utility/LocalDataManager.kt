package app.solution.dailyup.utility

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import app.solution.dailyup.model.ScheduleModel
import com.google.gson.Gson

class LocalDataManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(ConstKeys.SHARED_PREFERENCES, Context.MODE_PRIVATE)

    fun setData(key: String, value: String) = prefs.edit { putString(key, value) }
    fun getData(key: String): String? = prefs.getString(key, null)

    fun serialization(schedule: ScheduleModel) = Gson().toJson(schedule)
    fun <T> deserialization(json: String, type: Class<T>) = Gson().fromJson(json, type)
}