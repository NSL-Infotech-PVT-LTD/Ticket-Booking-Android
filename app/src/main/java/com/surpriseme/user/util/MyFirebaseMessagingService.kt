package com.surpriseme.user.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.surpriseme.user.R


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.notification != null && remoteMessage.data != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    greater_M_version(remoteMessage.notification?.title!!, remoteMessage.notification?.body!!)
            } else {
                    showNotification(remoteMessage.notification?.title!!, remoteMessage.notification?.body!!)
            }
        } else {
            Log.d("MESSAGE RECIEVER--->", remoteMessage.data.toString())
        }
    }

    // Sending intent to Notification activity....
    private fun showNotification(title: String, body: String) {
        val mBuilder =
            NotificationCompat.Builder(applicationContext)
        val notification: Notification
        notification =
            mBuilder.setSmallIcon(R.drawable.splash_logo).setTicker("New Notification").setWhen(0)
                .setAutoCancel(true)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun greater_M_version(title: String, messageData: String) {
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

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        notification = mBuilder.setSmallIcon(R.drawable.splash_logo) //.setAutoCancel(true)
            .setContentTitle(title)
            .setLargeIcon(bitmap)
            .setContentText(messageData)
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .build()

// notification.flags |= Notification.FLAG_AUTO_CANCEL;
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