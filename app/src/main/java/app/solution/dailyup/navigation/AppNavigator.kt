package app.solution.dailyup.navigation

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import app.solution.dailyup.event.NavigationEvent
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.view.AddScheduleActivity
import app.solution.dailyup.view.ChartActivity
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
                val intent = Intent(activity, AddScheduleActivity::class.java)

                if (event.scheduleModel != null) {
                    intent.apply {
                        putExtra(ConstKeys.SCHEDULE_MODEL, event.scheduleModel)
                    }
                }

                activityLauncher?.launch(intent)
            }
        }
    }
}