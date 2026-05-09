package app.solution.dailyup.utility

enum class RepeatTypeEnum(val displayName: String) {
    ONCE("한 번"),
    WEEKLY("매 주"),
    MONTHLY("매 월");

    companion object {
        fun convertToType(value: String): RepeatTypeEnum = when (value) {
            ONCE.name -> ONCE
            WEEKLY.name -> WEEKLY
            MONTHLY.name -> MONTHLY
            else -> ONCE
        }
    }
}
