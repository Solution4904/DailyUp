package app.solution.dailyup.model

import android.os.Parcelable
import app.solution.dailyup.R
import app.solution.dailyup.utility.RepeatTypeEnum
import app.solution.dailyup.utility.ScheduleTypeEnum
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ScheduleModel(
    val type: ScheduleTypeEnum = ScheduleTypeEnum.NORMAL,
    val repeat: RepeatTypeEnum = RepeatTypeEnum.ONCE,
    val id: String = UUID.randomUUID().toString(),
    val date: String = "",                         //  시작 날짜 (반복 기준일)
    val repeatUntil: String? = null,               //  종료 날짜 (null = infinity)
    val exceptions: List<String> = emptyList(),    //  제외된 날짜 리스트
    val title: String = "",
    val dec: String = "",
    val iconResId: Int? = R.drawable.ic_schedule_default,
    val progressMaxValue: Int? = 1,
    val progressStepValue: Int? = 1,
    val hour: Int = 0,
    val minute: Int = 0,
) : Parcelable