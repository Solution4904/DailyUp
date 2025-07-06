package app.solution.dailyup.model

import app.solution.dailyup.R
import app.solution.dailyup.utility.ScheduleTypeEnum
import java.util.UUID

data class ScheduleModel(
    val type: ScheduleTypeEnum = ScheduleTypeEnum.NORMAL,
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val dec: String = "",
    val iconResId: Int? = R.drawable.ic_schedule_default,
    val maxValue: Int? = 1,
    val valueStep: Int? = 1,
    val value: Int? = 0,
)