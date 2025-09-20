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
    val progressMaxValue: Int? = 1,
    val progressStepValue: Int? = 1,
    val progressValue: Int? = 0,
    val isCompleted: Boolean = false,
)