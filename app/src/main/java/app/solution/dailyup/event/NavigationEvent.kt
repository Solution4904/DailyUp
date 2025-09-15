package app.solution.dailyup.event

import app.solution.dailyup.model.ScheduleModel

sealed class NavigationEvent {
    object MoveToChartActivity : NavigationEvent()      //  차트 화면으로 이동
    object MoveToSettingActivity : NavigationEvent()        //  설정 화면으로 이동
    object MoveToAddScheduleActivity : NavigationEvent()        //  일정 추가로 이동
    data class MoveToEditScheduleActivity(val scheduleModel: ScheduleModel): NavigationEvent()   //  일정 수정으로 이동
}