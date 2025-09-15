package app.solution.dailyup

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import app.solution.dailyup.event.NavigationEvent
import app.solution.dailyup.utility.ConstKeys

class AppNavigator {
    fun navigate(
        activity: Activity,
        event: NavigationEvent,
        launcher: ActivityResultLauncher<Intent>? = null
    ) {
        when (event) {
            is NavigationEvent.MoveToChartActivity -> {
                activity.startActivity(Intent(activity, ChartActivity::class.java))
            }

            is NavigationEvent.MoveToSettingActivity -> {
                activity.startActivity(Intent(activity, SettingsActivity::class.java))
            }

            is NavigationEvent.MoveToAddScheduleActivity -> {
                launcher?.launch(
                    Intent(activity, AddScheduleActivity::class.java)
                )
            }

            is NavigationEvent.MoveToEditScheduleActivity -> {
                val intent = Intent(activity, AddScheduleActivity::class.java).apply {
                    with(event.scheduleModel) {
                        putExtra(ConstKeys.SCHEDULE_ID, id)
                        putExtra(ConstKeys.SCHEDULE_TITLE, title)
                        putExtra(ConstKeys.SCHEDULE_DATE, date)
                        putExtra(ConstKeys.SCHEDULE_DEC, dec)
                        putExtra(ConstKeys.SCHEDULE_ICONNAME, iconResId)
                        putExtra(ConstKeys.SCHEDULE_TYPE, type.toString())
                        putExtra(ConstKeys.SCHEDULE_MAXVALUE, processMaxValue)
                        putExtra(ConstKeys.SCHEDULE_VALUESTEP, processValueStep)
                        putExtra(ConstKeys.SCHEDULE_VALUE, processValue)
                    }
                }

                launcher?.launch(intent) ?: activity.startActivity(intent)
            }
        }
    }
}