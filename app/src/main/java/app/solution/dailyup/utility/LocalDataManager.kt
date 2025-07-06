package app.solution.dailyup.utility

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import app.solution.dailyup.model.ScheduleModel
import com.google.gson.Gson

class LocalDataManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(ConstKeys.SHARED_PREFERENCES, Context.MODE_PRIVATE)

    /**
     * Serialization
     *  1. List<ScheduleModel> 전달 받음
     *  2. 전달받은 데이터를 Json으로 String 변환
     * @param schedule
     */
    fun serialization(schedule: List<ScheduleModel>): String? = Gson().toJson(schedule)
    fun setData(key: String, value: String) = prefs.edit { putString(key, value) }

    /**
     * Deserialization
     *  1. String으로 데이터를 로드
     *  2. 로드된 데이터를 List<ScheduleModel>로 변환
     * @param json
     */
    fun deserialization(json: String): List<ScheduleModel> = Gson().fromJson(json, Array<ScheduleModel>::class.java).toMutableList()
    fun getData(key: String): String? = prefs.getString(key, null)
}