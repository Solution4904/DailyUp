package app.solution.dailyup.model

import androidx.annotation.ColorInt

data class ScheduleAchievedBox(
    val total: Int,
    val achieved: Int,
    val rate: Int,
)

data class ChartPageItem(
    val label: String,
    @ColorInt val indicatorColor: Int,
    val box: ScheduleAchievedBox,
)