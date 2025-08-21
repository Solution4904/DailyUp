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

    /**
     * 특정 날짜가 포함 되어 있는 기간의 처음 날짜와 마지막 날짜를 반환
     *
     * @param day 날짜
     * @param timePeriod 기간 (DAY, WEEK, MONTH)
     * @return Pair<LocalDate, LocalDate>
     */
    fun getTheCurrentDays(day: LocalDate, timePeriod: TimePeriod): Pair<LocalDate, LocalDate> {
        val startDay = when (timePeriod) {
            TimePeriod.DAY -> day
            TimePeriod.WEEK -> day.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            TimePeriod.MONTH -> day.with(TemporalAdjusters.firstDayOfMonth())
        }
        val endDay = when (timePeriod) {
            TimePeriod.DAY -> day
            TimePeriod.WEEK -> day.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
            TimePeriod.MONTH -> day.with(TemporalAdjusters.lastDayOfMonth())
        }

        return Pair(startDay, endDay)
    }
}

enum class TimePeriod {
    DAY,
    WEEK,
    MONTH
}