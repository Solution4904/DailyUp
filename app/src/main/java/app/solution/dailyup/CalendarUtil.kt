package app.solution.dailyup

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@RequiresApi(Build.VERSION_CODES.O)
class CalendarUtil {
    fun getWeeklyDates(baseDate: LocalDate = LocalDate.now()): List<LocalDate> {
        val weekStart = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        return (0..6).map { weekStart.plusDays(it.toLong()) }
    }
}