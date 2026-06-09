package app.solution.dailyup.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.solution.dailyup.utility.LocalDataManager
import app.solution.dailyup.utility.ScheduleAlarmScheduler
import app.solution.dailyup.utility.nextOccurrenceAfter
import java.time.LocalDate

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

    //  부팅으로 인해 리셋된 모든 일정 재등록
    private fun reStartAllSchedules(context: Context) {
        val today = LocalDate.now()

        LocalDataManager.getSchedules().forEach { schedule ->
            val next = schedule.nextOccurrenceAfter(today.minusDays(1)) ?: return@forEach

            ScheduleAlarmScheduler.add(context, schedule, next)
        }
    }
}