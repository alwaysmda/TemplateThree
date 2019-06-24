package com.xodus.templatetwo.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.xodus.templatetwo.R
import com.xodus.templatetwo.http.*
import com.xodus.templatetwo.main.ApplicationClass
import com.xodus.templatetwo.main.BaseActivity
import com.xodus.templatetwo.main.Constant
import com.xodus.templatetwo.main.log

class MessagingService : FirebaseMessagingService(), OnResponseListener {

    private val appClass = ApplicationClass.getInstance()
    private val client = Client.getInstance()


    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        token?.let {
            log("FCM", "onNewToken", token)
            appClass.setPref(Constant.PREF_FCM_TOKEN, it)
            client.request(API.UpdateFCMToken(it, this))
        }
    }

    override fun onMessageReceived(msg: RemoteMessage?) {
        super.onMessageReceived(msg)
        msg?.let { message ->
            //Sender ID
            log("FCM", "onMessageReceived", "SENDER ID", message.from)

            //Firebase Console -> Additional Options -> Custom Data
            //Ex : {custom data key=custom data value}
            //set `class` as key and `ActivityName` as value to open activity onclick
            //ser `action` as key and action value as you want and parse in activity
            log("FCM", "onMessageReceived", "DATA", message.data)

            //Firebase Console -> Message -> Message Text = getBody()
            //Firebase Console -> Message -> Message Title = getTitle()
            message.notification?.let {
                log("FCM", "onMessageReceived", "BODY", it.body, "TITLE", it.title)
            }

            sendNotification(message)
        }
    }

    override fun onResponse(response: Response) {

    }

    override fun onProgress(request: Request, bytesWritten: Long, totalSize: Long, percent: Int) {

    }

    private fun sendNotification(message: RemoteMessage) {
        val intent = Intent(this, BaseActivity::class.java)
        if (message.data.isNotEmpty()) {
            intent.action = message.data["action"]
            for (key in message.data.keys) {
                intent.putExtra(key, message.data[key])
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(appClass, 323 , intent,PendingIntent.FLAG_ONE_SHOT)
        val channelId = "1"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(message.notification!!.title)
            .setContentText(message.notification!!.body)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(appClass, R.color.colorAccent))
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(926 , notificationBuilder.build())
    }
}