package app.solution.dailyup.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.solution.dailyup.R
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.NotificationHelper
import app.solution.dailyup.view.MainActivity

class ScheduleAlarmReceiver : BroadcastReceiver() {
    @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra(ConstKeys.SCHEDULE_ID) ?: return
        val title = intent.getStringExtra(ConstKeys.SCHEDULE_TITLE).orEmpty()
        val dec = intent.getStringExtra(ConstKeys.SCHEDULE_DEC).orEmpty()

        val contentPendingIntent = buildContentPendingIntent(context, id)
        val notification = buildNotification(context, title, dec, contentPendingIntent)

        if (!hasNotificationPermission(context)) return

        NotificationManagerCompat.from(context).notify(id.hashCode(), notification)
    }

    private fun buildContentPendingIntent(context: Context, id: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return PendingIntent.getActivity(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildNotification(context: Context, title: String, dec: String, contentPendingIntent: PendingIntent) = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_SCHEDULE_ALARM)
        .setSmallIcon(R.drawable.ic_schedule_default)
        .setContentTitle(title)
        .setContentText(dec)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .build()

    private fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        return ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}