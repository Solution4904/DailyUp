package app.solution.dailyup.utility

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    const val CHANNEL_ID_SCHEDULE_ALARM = "schedule_alram_channel"
    const val CHANNEL_NAME_SCHEDULE_ALARM = "일정 알림"
    const val CHANNEL_DEC_SCHEDULE_ALARM = "일정 알림을 위한 채널입니다."

    fun createChannels(context: Context) {
        val scheduleAlarmChannel = NotificationChannelCompat.Builder(
            CHANNEL_ID_SCHEDULE_ALARM,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName(CHANNEL_NAME_SCHEDULE_ALARM)
            .setDescription(CHANNEL_DEC_SCHEDULE_ALARM)
            .build()

        NotificationManagerCompat.from(context)
            .createNotificationChannel(scheduleAlarmChannel)
    }
}