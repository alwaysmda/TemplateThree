package util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.core.app.NotificationCompat
import com.xodus.templatethree.R
import main.ApplicationClass

class Notification(val appClass: ApplicationClass) {
    private val NOTIFICATION_CHANNEL_ID = "10001"
    private val NOTIFICATION_ID = 926
    private var mNotificationManager: NotificationManager = appClass.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(appClass, NOTIFICATION_CHANNEL_ID)

    fun create(message: String, title: String, requestCode: Int = NOTIFICATION_ID, intent: Intent? = null, color: Int = Color.YELLOW, vibrate: Boolean = false, silent: Boolean = false) {
        mBuilder.setSmallIcon(R.drawable.ic_launcher)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setContentTitle(title)
            .setTicker(title)
            .setAutoCancel(true)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(false)
            .setSound(if (silent) null else Settings.System.DEFAULT_NOTIFICATION_URI)
        intent?.let { mBuilder.setContentIntent(PendingIntent.getActivity(appClass, 323, intent, PendingIntent.FLAG_UPDATE_CURRENT)) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = color
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = if (vibrate) longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400) else longArrayOf(0)
            notificationChannel.lockscreenVisibility = View.VISIBLE
            notificationChannel.enableVibration(vibrate)
            notificationChannel.setBypassDnd(false)
            if (silent) {
                notificationChannel.setSound(null, null)
            }
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(requestCode, mBuilder.build())
    }

    fun update(message: String, title: String, requestCode: Int = NOTIFICATION_ID) {
        mBuilder.setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setSound(null)
            .setOngoing(false)
            .setVibrate(null)
        mNotificationManager.notify(requestCode, mBuilder.build())
    }

    fun close(requestCode: Int = NOTIFICATION_ID) {
        mNotificationManager.cancel(requestCode)
    }

    fun closeAll() {
        mNotificationManager.cancelAll()
    }
}