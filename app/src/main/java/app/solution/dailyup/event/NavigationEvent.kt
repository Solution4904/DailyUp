package app.solution.dailyup.event

import app.solution.dailyup.model.ScheduleModel

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
     * 일정 추가 화면으로 이동
     * @constructor Create empty Move to add schedule activity
     */
    object MoveToAddScheduleActivity : NavigationEvent()        //  일정 추가로 이동

    /**
     * Move to edit schedule activity
     * 일정 수정 화면으로 이동
     * @property scheduleModel
     * @constructor Create empty Move to edit schedule activity
     */
    data class MoveToEditScheduleActivity(val scheduleModel: ScheduleModel): NavigationEvent()   //  일정 수정으로 이동
}