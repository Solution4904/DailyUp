package app.solution.dailyup.event

import app.solution.dailyup.model.ScheduleModel

sealed class MainUiEvent {
    /**
     * Show delete schedule dialog
     * 일정 제거 다이얼로그 팝업 이벤트
     * @property scheduleModel 제거 대상
     * @constructor Create empty Show delete schedule dialog
     */
    data class ShowDeleteScheduleDialog(val scheduleModel: ScheduleModel) : MainUiEvent()

    /**
     * Schedule complete
     * 체크 방식 일정 완료 이벤트
     * @property scheduleModel 완료 대상
     * @constructor Create empty Schedule complete
     */
    data class ScheduleComplete(val scheduleModel: ScheduleModel) : MainUiEvent()

    /**
     * Schedule increase process
     * 할당 방식 일정 진행률 증가 이벤트
     * @property scheduleModel 증가 대상
     * @constructor Create empty Schedule increase process
     */
    data class ScheduleIncreaseProcess(val scheduleModel: ScheduleModel) : MainUiEvent()
}