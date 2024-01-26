package com.willow.android.mobile.services

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.models.pages.FixtureModel


const val N_CHANNEL_ID = "WillowNChannelId"
const val N_CHANNEL_NAME = "WillowNChannelName"
const val N_CHANNEL_DESCRIPTION = "Notification for Matches"


class LocalNotificationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("LocalNotification", "Received Alarm")
        var id = intent.getIntExtra("NOTIFICATION_ID", 0)
        var title = intent.getStringExtra("NOTIFICATION_TITLE")

        title?.let { LocalNotificationService.triggerNotification(context, id, it) }

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // reset all alarms
        } else {
            // perform your scheduled task here (eg. send alarm notification)
        }
    }
}

object LocalNotificationService {
    var scheduledNotificationIds: HashMap<String, String> = hashMapOf()

    /** should execute this code as soon as app starts.
     * It's safe to call this repeatedly because creating an existing notification channel performs no operation.*/
    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(N_CHANNEL_ID, N_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description =  N_CHANNEL_DESCRIPTION
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun triggerNotification(context: Context, id: Int, title: String) {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        val notification = NotificationCompat.Builder(context, N_CHANNEL_ID)
            .setSmallIcon(com.google.android.exoplayer2.ui.R.drawable.exo_notification_small_icon)
            .setContentTitle(title)
                //            .setContentText(localNotification.message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }


    fun scheduleLocalNotificationAlarm(context: Context, fixtureModel: FixtureModel) {
        val intentRequestCode = fixtureModel.match_id.toInt()

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val alarmIntent = Intent(context, LocalNotificationAlarmReceiver::class.java)
        alarmIntent.putExtra("NOTIFICATION_ID", fixtureModel.match_id)
        alarmIntent.putExtra("NOTIFICATION_TITLE", fixtureModel.notificationTitle)
        val pendingIntent = PendingIntent.getBroadcast(context, intentRequestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager!!.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (fixtureModel.start_date_time_ts * 1000), pendingIntent)
        } else {
            manager!!.set(AlarmManager.RTC_WAKEUP, (fixtureModel.start_date_time_ts * 1000), pendingIntent)
        }
         */
        manager!!.set(AlarmManager.RTC_WAKEUP, (fixtureModel.start_date_time_ts * 1000), pendingIntent)

        addToScheduledNotificationList(fixtureModel)
    }

    fun removeLocalNotificationAlarm(context: Context, fixtureModel: FixtureModel) {
        val intentRequestCode = fixtureModel.match_id.toInt()
        val alarmIntent = Intent(context, LocalNotificationAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, intentRequestCode, alarmIntent, PendingIntent.FLAG_NO_CREATE)

        if (pendingIntent != null) {
            pendingIntent.cancel()
            removeFromScheduledNotificationList(fixtureModel)
        }
    }

    private fun addToScheduledNotificationList(fixtureModel: FixtureModel) {
        if (scheduledNotificationIds.containsKey(fixtureModel.match_id)) { return }
        scheduledNotificationIds[fixtureModel.match_id] = fixtureModel.start_date_time_ts.toString()
        addUpdatedNotificationListToLocalStorage()
    }

    private fun removeFromScheduledNotificationList(fixtureModel: FixtureModel) {
        if (scheduledNotificationIds.containsKey(fixtureModel.match_id)) {
            scheduledNotificationIds.remove(fixtureModel.match_id)
            addUpdatedNotificationListToLocalStorage()
        }
    }

    private fun addUpdatedNotificationListToLocalStorage() {
        StorageService.storeLocalNotificationIds(scheduledNotificationIds)
    }

    fun initNotificationListFromLocalStorage() {
        scheduledNotificationIds = StorageService.getLocalNotificationIds()

        // @ToDo - Remove Old Notifications Notifications from Local Storage and Even from PendingIntents if available any
    }
}