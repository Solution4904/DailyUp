package app.solution.dailyup.model

data class ScheduleProgressModel(
    val scheduleId: String,
    val date: String,    //  어느 날짜의 상태인지 구분
    val isComplete: Boolean = false,
    val progressValue: Int = 0,
) {
    val key: String get() = "$scheduleId@$date"
}
