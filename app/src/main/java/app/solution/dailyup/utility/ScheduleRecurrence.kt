package app.solution.dailyup.utility

import app.solution.dailyup.model.ScheduleModel
import java.time.LocalDate

//  해당 날짜에 이 일정의 발생 여부
fun ScheduleModel.occursOn(target: LocalDate): Boolean {
    if (date.isBlank()) return false
    val start = runCatching { LocalDate.parse(date) }.getOrNull() ?: return false
    if (target.isBefore(start)) return false

    val until = repeatUntil?.let {
        runCatching {
            LocalDate.parse(it)
        }.getOrNull()
    }

    //  todo:???
    if (until != null && target.isAfter(until)) return false

    //  todo:???
    if (exceptions.contains(target.toString())) return false

    return when (repeat) {
        RepeatTypeEnum.ONCE -> target == start
        RepeatTypeEnum.WEEKLY -> start.dayOfWeek == target.dayOfWeek
        RepeatTypeEnum.MONTHLY -> start.dayOfMonth == target.dayOfMonth
    }
}

//  todo:???
//  특정 날짜 범위에서 발생하는 모든 회차 날짜
fun ScheduleModel.occurrencesIn(range: ClosedRange<LocalDate>): List<LocalDate> {
    val list = mutableListOf<LocalDate>()
    var tempDate = range.start
    while (!tempDate.isAfter(range.endInclusive)) {
        if (occursOn(tempDate)) list.add(tempDate)
        tempDate = tempDate.plusDays(1)
    }

    return list
}

//  기준 시각 이후 바로 다음 발생일 (알림 재예약)
fun ScheduleModel.nextOccurrenceAfter(after: LocalDate): LocalDate? {
    val start = runCatching {
        LocalDate.parse(date)
    }.getOrNull() ?: return null

    val until = repeatUntil?.let {
        runCatching {
            LocalDate.parse(it)
        }.getOrNull()
    }

    val limit = until ?: after.plusYears(2)

    var tempDate = if (after.isBefore(start)) start else after.plusDays(1)
    //  todo:???
    while (!tempDate.isAfter(limit)) {
        if (occursOn(tempDate)) return tempDate
        tempDate = tempDate.plusDays(1)
    }

    return null
}