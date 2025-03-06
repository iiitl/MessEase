package com.theayushyadav11.MessEase.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.theayushyadav11.MessEase.MainActivity
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDatabase
import com.theayushyadav11.MessEase.utils.Mess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val index = intent.getIntExtra("type", 0)
        val mess = Mess(context)

        MainScope().launch(Dispatchers.IO) {
            showNotification(context, index, mess)
        }
    }

    private suspend fun showNotification(context: Context, index: Int, mess: Mess) {
        try {
            val database = MenuDatabase.getDatabase(context).menuDao()
            val menu = database.getMenu()
            val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

            val mealType = menu.menu[day].particulars[index].type
            val mealFood = menu.menu[day].particulars[index].food
            val mealTime = menu.menu[day].particulars[index].time

            val title = "Reminder for $mealType"
            val message = "Today's $mealType is\n$mealFood\nTiming: $mealTime"

            val targetIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                index,
                targetIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, "DailyNotification")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setContentIntent(pendingIntent)

            if (hasNotificationPermission(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                NotificationManagerCompat.from(context).notify(index, builder.build())
            }
        } catch (e: Exception) {
            mess.log("Error showing notification: ${e.message}")
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}