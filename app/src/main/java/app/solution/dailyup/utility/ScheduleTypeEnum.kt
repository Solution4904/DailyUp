package app.solution.dailyup.utility

enum class ScheduleTypeEnum(val displayName: String) {
    NORMAL("체크 방식"),
    COUNTING("할당 방식");

    companion object {
        fun convert(value: String): ScheduleTypeEnum = when (value) {
            NORMAL.name -> NORMAL
            COUNTING.name -> COUNTING
            else -> NORMAL
        }

        fun convert(value: Int): ScheduleTypeEnum = when (value) {
            NORMAL.ordinal -> NORMAL
            COUNTING.ordinal -> COUNTING
            else -> NORMAL
        }
    }
}