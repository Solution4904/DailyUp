package app.solution.dailyup.model

import java.time.LocalDate

data class ScheduleOccurrence(
    val source: ScheduleModel,               //  원본 일정
    val date: LocalDate,                    //  이 회차의 날짜
    val progress: ScheduleProgressModel     //  이 회차의 진행도
)
