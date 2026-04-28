package app.solution.dailyup.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.solution.dailyup.utility.LocalDataManager
import app.solution.dailyup.utility.ScheduleAlarmScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            //  부팅 후 잠금 해제 시
            Intent.ACTION_BOOT_COMPLETED,
            //  앱 업데이트 시
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                reStartAllSchedules(context)
            }
        }
    }

    private fun reStartAllSchedules(context: Context) {
        LocalDataManager.getSchedules().forEach { schedule ->
            ScheduleAlarmScheduler.add(context, schedule)
        }
    }
}