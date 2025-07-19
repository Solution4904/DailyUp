package app.solution.dailyup.utility

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import app.solution.dailyup.model.ScheduleModel
import com.google.gson.Gson

class LocalDataManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(ConstKeys.SHARED_PREFERENCES, Context.MODE_PRIVATE)

    /*
        */
    /**
     * Serialization
     *  1. List<ScheduleModel> 전달 받음
     *  2. 전달받은 데이터를 Json으로 String 변환
     * @param schedule
     *//*

    fun serialization(schedule: List<ScheduleModel>): String? = Gson().toJson(schedule)
    fun setData(key: String, value: String) = prefs.edit { putString(key, value) }

    */
    /**
     * Deserialization
     *  1. String으로 데이터를 로드
     *  2. 로드된 데이터를 List<ScheduleModel>로 변환
     * @param json
     *//*

    fun deserialization(json: String): List<ScheduleModel> = Gson().fromJson(json, Array<ScheduleModel>::class.java).toMutableList()
    fun getData(key: String): String? = prefs.getString(key, null)

*/

    /**
     * Get schedules
     *  1. SharedPreferences에서 데이터를 로드
     *  2. 로드된 데이터를 List<ScheduleModel>로 변환
     *  3. List<ScheduleModel>를 반환
     *  4. 로드된 데이터가 없을 경우 빈 리스트를 반환
     * @return List<ScheduleModel>
     */
    fun getSchedules(): List<ScheduleModel> {
        val scheduleDatas = prefs.getString(ConstKeys.SCHEDULE_LIST, null)

        scheduleDatas?.let {
            TraceLog(message = "Schedule 로드 성공")

            return Gson().fromJson(it, Array<ScheduleModel>::class.java).toList()
        }

        TraceLog(message = "Schedule 로드 실패")
        return listOf()
    }

    /**
     * Save schedules
     * 1. List<ScheduleModel>을 Json으로 변환
     * 2. SharedPreferences에 데이터를 저장
     * @param targetData
     */
    fun saveSchedules(targetData: List<ScheduleModel>) {
        val data = Gson().toJson(targetData)

        prefs.edit { putString(ConstKeys.SCHEDULE_LIST, data) }

        TraceLog(message = "Schedule 저장 -> $data")
    }
}