package app.solution.dailyup.utility

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import app.solution.dailyup.CalendarUtil
import app.solution.dailyup.TimePeriod
import app.solution.dailyup.model.ScheduleModel
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDataManager {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(ConstKeys.SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSchedulesForPeriod(day: LocalDate, timePeriod: TimePeriod): List<ScheduleModel> {
        val scheduleDatas = prefs.getString(ConstKeys.SCHEDULE_LIST, null)?.let {
            Gson().fromJson(it, Array<ScheduleModel>::class.java).toList()
        }

        scheduleDatas?.let {
            TraceLog(message = "Schedule 로드 성공")

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val datePair = CalendarUtil().getTheCurrentDays(day, timePeriod)
            val startDate = LocalDate.parse(datePair.first.toString(), formatter)
            val endDate = LocalDate.parse(datePair.second.toString(), formatter)

            return scheduleDatas.filter { schedule ->
                val scheduleDate = LocalDate.parse(schedule.date, formatter)
                (scheduleDate.isEqual(startDate) || scheduleDate.isAfter(startDate)) &&
                        (scheduleDate.isEqual(endDate) || scheduleDate.isBefore(endDate))
            }
        }

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