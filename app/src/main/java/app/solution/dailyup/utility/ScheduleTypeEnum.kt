package app.solution.dailyup.utility

enum class ScheduleTypeEnum(val displayName: String) {
    NORMAL("단발성 목표"),
    COUNTING("누적형 목표");

    companion object {
        fun convertToType(value: String): ScheduleTypeEnum = when (value) {
            NORMAL.name -> NORMAL
            COUNTING.name -> COUNTING
            else -> NORMAL
        }

        fun convertToType(value: Int): ScheduleTypeEnum = when (value) {
            NORMAL.ordinal -> NORMAL
            COUNTING.ordinal -> COUNTING
            else -> NORMAL
        }
    }
}