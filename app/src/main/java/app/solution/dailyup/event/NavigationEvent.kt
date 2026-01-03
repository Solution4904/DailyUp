package app.solution.dailyup.event

import app.solution.dailyup.model.ScheduleModel
import java.time.LocalDate

sealed class NavigationEvent {
    /**
     * Move to chart activity
     * 차트 화면으로 이동
     * @constructor Create empty Move to chart activity
     */
    object MoveToChartActivity : NavigationEvent()      //  차트 화면으로 이동

    /**
     * Move to setting activity
     * 설정 화면으로 이동
     * @constructor Create empty Move to setting activity
     */
    object MoveToSettingActivity : NavigationEvent()        //  설정 화면으로 이동

    /**
     * Move to add schedule activity
     * 일정 추가로 이동
     * @property scheduleModel 일정 수정일 경우 수정할 데이터
     * @constructor Create empty Move to add schedule activity
     */
    data class MoveToAddScheduleActivity(val scheduleModel: ScheduleModel? = null, val selectedDate: LocalDate? = null) : NavigationEvent()        //  일정 추가로 이동
}