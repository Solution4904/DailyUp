package app.solution.dailyup

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import app.solution.dailyup.event.NavigationEvent
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.view.AddScheduleActivity
import app.solution.dailyup.view.SettingsActivity

class AppNavigator {
    fun navigate(
        activity: Activity,
        event: NavigationEvent,
        activityLauncher: ActivityResultLauncher<Intent>? = null
    ) {
        when (event) {
            is NavigationEvent.MoveToChartActivity -> {
                activity.startActivity(Intent(activity, ChartActivity::class.java))
            }

            is NavigationEvent.MoveToSettingActivity -> {
                activity.startActivity(Intent(activity, SettingsActivity::class.java))
            }

            is NavigationEvent.MoveToAddScheduleActivity -> {
                if (event.scheduleModel == null) {
                    val intent = Intent(activity, AddScheduleActivity::class.java).apply {
                        putExtra(ConstKeys.SCHEDULE_DATE, event.selectedDate.toString())
                    }
                    activityLauncher?.launch(intent)
                } else {
                    val intent = Intent(activity, AddScheduleActivity::class.java).apply {
                        event.scheduleModel.let {
                            putExtra(ConstKeys.SCHEDULE_ID, event.scheduleModel.id)
                            putExtra(ConstKeys.SCHEDULE_TITLE, event.scheduleModel.title)
                            putExtra(ConstKeys.SCHEDULE_DATE, event.scheduleModel.date)
                            putExtra(ConstKeys.SCHEDULE_DEC, event.scheduleModel.dec)
                            putExtra(ConstKeys.SCHEDULE_ICONNAME, event.scheduleModel.iconResId)
                            putExtra(ConstKeys.SCHEDULE_TYPE, event.scheduleModel.type.name)
                            putExtra(ConstKeys.SCHEDULE_MAXVALUE, event.scheduleModel.progressMaxValue)
                            putExtra(ConstKeys.SCHEDULE_VALUESTEP, event.scheduleModel.progressStepValue)
                            putExtra(ConstKeys.SCHEDULE_VALUE, event.scheduleModel.progressValue)
                        }
                    }
                    activity.startActivity(intent)
                }
            }
        }
    }
}