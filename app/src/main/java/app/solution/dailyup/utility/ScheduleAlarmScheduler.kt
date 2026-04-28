package app.solution.dailyup.utility

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.receiver.ScheduleAlarmReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object ScheduleAlarmScheduler {
    // 알람 등록
    fun add(context: Context, model: ScheduleModel) {
        val triggerMillis = computeTriggerMillis(model) ?: return
        //  등록하려는 시간이 현재 시간보다 과거일 경우 return
        if (triggerMillis <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, model)

        //  '정확한 알람' 권한 Boolean
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        if (canExact) {
            //  정확한 알람
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        } else {
            //  부정확한 알람 (권한이 없으면 fallback)
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
    }

    //  알람 취소
    fun cancel(context: Context, model: ScheduleModel) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent(context, model))
    }

    //  Pending Intent 생성/반환
    private fun buildPendingIntent(context: Context, model: ScheduleModel): PendingIntent {
        val intent = Intent(context, ScheduleAlarmReceiver::class.java).apply {
            putExtra(ConstKeys.SCHEDULE_ID, model.id)
            putExtra(ConstKeys.SCHEDULE_TITLE, model.title)
            putExtra(ConstKeys.SCHEDULE_DEC, model.dec)
        }

        return PendingIntent.getBroadcast(
            context,
            model.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    //  시간값 변환
    private fun computeTriggerMillis(model: ScheduleModel): Long? {
        if (model.date.isBlank()) return null

        //  ≒ try-catch
        return runCatching {
            val date = LocalDate.parse(model.date)
            LocalDateTime.of(date, LocalTime.of(model.hour, model.minute))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.getOrNull()
    }
}