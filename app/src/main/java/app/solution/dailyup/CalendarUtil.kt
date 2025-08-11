package app.solution.dailyup

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class CalendarUtil {
    fun getWeeklyDates(baseDate: LocalDate = LocalDate.now()): List<LocalDate> {
        val weekStart = baseDate.with(DayOfWeek.SUNDAY)
        return (0..6).map { weekStart.plusDays(it.toLong()) }
    }
}