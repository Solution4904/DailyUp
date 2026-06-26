package app.solution.dailyup.data

import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.model.ScheduleProgressModel
import app.solution.dailyup.utility.TimePeriod
import java.time.LocalDate

//  todo:???
interface ScheduleRepository {
    suspend fun getSchedules(): List<ScheduleModel>
    suspend fun getSchedulesForPeriod(day: LocalDate, timePeriod: TimePeriod): List<ScheduleModel>
    suspend fun saveSchedules(data: List<ScheduleModel>)

    suspend fun getProgressMap(): Map<String, ScheduleProgressModel>
    suspend fun saveProgressMap(map: Map<String, ScheduleProgressModel>)
    suspend fun upsertProgress(progress: ScheduleProgressModel)
    suspend fun deleteProgress(scheduleId: String)
}