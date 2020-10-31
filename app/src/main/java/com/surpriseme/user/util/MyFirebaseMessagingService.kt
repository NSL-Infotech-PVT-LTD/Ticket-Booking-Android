package com.surpriseme.user.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.surpriseme.user.R
import com.surpriseme.user.activity.mainactivity.MainActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.notification != null && remoteMessage.data != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val id = remoteMessage.data.get("target_id")
//                val data_type = remoteMessage.data.get("data_type")
            val target_Model = remoteMessage.data.get("target_model")

                    greater_M_version(remoteMessage.notification?.title!!, remoteMessage.notification?.body!!,id!!,target_Model!!)
            } else {
                val id = remoteMessage.data.get("target_id")
//                val data_type = remoteMessage.data.get("data_type")
            val target_Model = remoteMessage.data.get("target_model")

                    showNotification(remoteMessage.notification?.title!!, remoteMessage.notification?.body!!,id!!,target_Model!!)
            }
        } else {
            Log.d("MESSAGE RECIEVER--->", remoteMessage.data.toString())
        }
    }

    // if api lever lower than "O"....
    private fun showNotification(title: String, body: String, id:String,target_Model:String) {
        var intent: Intent? = null
        if (target_Model.equals(Constants.TARGET_MODEL_BOOKING)) {
            intent = Intent(this, MainActivity::class.java)
            intent.putExtra("bookingId",id)
        } else if (target_Model.equals(Constants.TARGET_MODEL_MESSAGE)) {
            intent = Intent(this, MainActivity::class.java)
            intent.putExtra("chatId", id)
        }

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(intent!!)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val mBuilder =
            NotificationCompat.Builder(applicationContext)
        val notification: Notification
        notification =
            mBuilder.setSmallIcon(R.drawable.splash_logo).setTicker("New Notification").setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.splash_logo)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.splash_logo))
                .setContentText(body)
                .setAutoCancel(true)
                .build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }

    // if api lever greate than "O"....
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun greater_M_version(title: String, messageData: String,id:String,target_Model:String) {

        var intent: Intent? = null
        if (target_Model.equals(Constants.TARGET_MODEL_BOOKING)) {
            intent = Intent(this, MainActivity::class.java)
            intent.putExtra("bookingId",id)

        } else if (target_Model.equals(Constants.TARGET_MODEL_MESSAGE)) {
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("chatId", id)
        }

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntentWithParentStack(intent!!)
        val notification: Notification
        val channelId = applicationContext.getString(R.string.default_notification_channel_id)
        var channel: NotificationChannel? = null
        // channel = new NotificationChannel(getApplicationContext().getPackageName(), getApplicationContext().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        channel = NotificationChannel(channelId, "a", NotificationManager.IMPORTANCE_HIGH)
        channel.enableVibration(true)
        channel.description = messageData
        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        val mBuilder = NotificationCompat.Builder(this, channelId)
        mBuilder.setChannelId(channelId)
        val bitmap =
            BitmapFactory.decodeResource(applicationContext.resources, R.drawable.splash_logo)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notifyPendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        notification = mBuilder.setSmallIcon(R.drawable.splash_logo) //.setAutoCancel(true)
            .setContentTitle(title)
            .setContentIntent(notifyPendingIntent)
            .setLargeIcon(bitmap)
            .setContentText(messageData)
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .build()
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel)
                notificationManager.notify(1, notification)
            }
        }
    }
}