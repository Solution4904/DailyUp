package app.solution.dailyup.data

import android.content.SharedPreferences
import androidx.core.content.edit
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.model.ScheduleProgressModel
import app.solution.dailyup.utility.CalendarUtil
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.ConstKeys.SCHEDULE_PROGRESS_LIST
import app.solution.dailyup.utility.TimePeriod
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//  todo:???
class LocalScheduleRepository(
    private val prefs: SharedPreferences,
    private val gson: Gson = Gson()
) : ScheduleRepository {
    override suspend fun getSchedules(): List<ScheduleModel> = withContext(Dispatchers.IO) {
        val raw = prefs.getString(ConstKeys.SCHEDULE_LIST, null) ?: return@withContext emptyList()
        gson.fromJson(raw, Array<ScheduleModel>::class.java).toList()
    }

    override suspend fun getSchedulesForPeriod(day: LocalDate, timePeriod: TimePeriod): List<ScheduleModel> = withContext(Dispatchers.IO) {
        if (timePeriod == TimePeriod.TOTAL) return@withContext getSchedules()

        val scheduleDatas = prefs.getString(ConstKeys.SCHEDULE_LIST, null)?.let {
            gson.fromJson(it, Array<ScheduleModel>::class.java).toList()
        }

        scheduleDatas?.let {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val datePair = CalendarUtil().getTheCurrentDays(day, timePeriod)
            val startDate = LocalDate.parse(datePair.first.toString(), formatter)
            val endDate = LocalDate.parse(datePair.second.toString(), formatter)

            scheduleDatas.filter { schedule ->
                val scheduleDate = LocalDate.parse(schedule.date, formatter)
                (scheduleDate.isEqual(startDate) || scheduleDate.isAfter(startDate)) &&
                        (scheduleDate.isEqual(endDate) || scheduleDate.isBefore(endDate))
            }
        }

        listOf()
    }

    override suspend fun saveSchedules(data: List<ScheduleModel>) = withContext(Dispatchers.IO) {
        prefs.edit { putString(ConstKeys.SCHEDULE_LIST, gson.toJson(data)) }
    }

    override suspend fun getProgressMap(): Map<String, ScheduleProgressModel> = withContext(Dispatchers.IO) {
        val raw = prefs.getString(SCHEDULE_PROGRESS_LIST, null) ?: return@withContext emptyMap()

        //  todo:???
        gson.fromJson(raw, Array<ScheduleProgressModel>::class.java).toList().associateBy { it.key }
    }

    override suspend fun saveProgressMap(map: Map<String, ScheduleProgressModel>) = withContext(Dispatchers.IO) {
        prefs.edit { putString(ConstKeys.SCHEDULE_PROGRESS_LIST, gson.toJson(map.values.toList())) }
    }

    override suspend fun upsertProgress(progress: ScheduleProgressModel) {
        val map = getProgressMap().toMutableMap()

        map[progress.key] = progress

        saveProgressMap(map)
    }

    override suspend fun deleteProgress(scheduleId: String) {
        val map = getProgressMap().filterValues { it.scheduleId != scheduleId }

        saveProgressMap(map)
    }
}