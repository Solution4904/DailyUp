package app.solution.dailyup.model

import app.solution.dailyup.R
import app.solution.dailyup.utility.ScheduleTypeEnum
import java.util.UUID

data class ScheduleModel(
    val type: ScheduleTypeEnum = ScheduleTypeEnum.NORMAL,
    val id: String = UUID.randomUUID().toString(),
    val date: String = "",
    val title: String = "",
    val dec: String = "",
    val iconResId: Int? = R.drawable.ic_schedule_default,
    val processMaxValue: Int? = 1,
    val processValueStep: Int? = 1,
    val processValue: Int? = 0,
)