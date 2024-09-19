package com.theayushyadav11.MessEase.notifications


import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.theayushyadav11.MessEase.MainActivity
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDatabase
import com.theayushyadav11.MessEase.utils.Constants.Companion.TAG
import com.theayushyadav11.MessEase.utils.Mess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val targetIntent = Intent(it, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val mess= Mess(context)
            val times = listOf(mess.get("bt","7:30"),mess.get("lt","12:0"),mess.get("st","16:30"),mess.get("dt","19:0"))
            val currentTime = ""+Date().hours +":"+ Date().minutes                     // getCurrentTime(context)
            mess.log(times)
            mess.log(currentTime)
            if(times.contains(currentTime)) {
                val pendingIntent = PendingIntent.getActivity(
                    it, 0, targetIntent, PendingIntent.FLAG_IMMUTABLE
                )
                GlobalScope.launch(Dispatchers.IO)
                {

                    val database = MenuDatabase.getDatabase(context).menuDao()
                    val menu = database.getMenu()
                    val v = intent?.getIntExtra("type", 3)
                    val day = getDayOfWeek()
                    val title = ("Reminder for ${menu.menu[day].particulars[v!!].type}")
                    val message =
                        ("Today's ${menu.menu[day].particulars[v].type} is\n${menu.menu[day].particulars[v].food}\nTiming: ${menu.menu[day].particulars[v].time}")
                    val builder = NotificationCompat.Builder(it, "DailyNotification")
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText(message)
                        )
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                        .setContentIntent(pendingIntent)

                    val notificationManager = NotificationManagerCompat.from(it)

                    if (ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@launch
                    }

                    notificationManager.notify(123, builder.build())


                }
            }


        }
    }

    fun getDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return dayOfWeek
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(context: Context): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return currentTime.format(formatter)
    }

}
