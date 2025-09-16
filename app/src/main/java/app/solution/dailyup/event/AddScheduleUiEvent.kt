package app.solution.dailyup.event

sealed class AddScheduleUiEvent {
    /**
     * Show date picker
     * 날짜 선택 다이얼로그 팝업 이벤트
     * @constructor Create empty Show date picker
     */
    object ShowDatePicker : AddScheduleUiEvent()

    /**
     * Show icon picker
     * 아이콘 선택 다이얼로그 팝업 이벤트
     * @constructor Create empty Show icon picker
     */
    object ShowIconPicker : AddScheduleUiEvent()

    /**
     * Show type picker
     * 타입 선택 다이얼로그 팝업 이벤트
     * @constructor Create empty Show type picker
     */
    object ShowTypePicker : AddScheduleUiEvent()

    /**
     * Schedule save
     * 일정 저장 이벤트
     * @constructor Create empty Schedule save
     */
    object ScheduleSave : AddScheduleUiEvent()

    /**
     * Schedule cancel
     * 일정 등록/수정 취소 이벤트
     * @constructor Create empty Schedule cancel
     */
    object ScheduleCancel : AddScheduleUiEvent()
}