package com.theayushyadav11.MessEase.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.widget.RemoteViews
import com.theayushyadav11.MessEase.MainActivity
import com.theayushyadav11.MessEase.Models.DayMenu
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Mess

class MenuWidget : AppWidgetProvider() {

    private lateinit var mess: Mess

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        mess = Mess(context)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.menu_widget)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.container, pendingIntent)

        getMenu { dayMenu ->
            views.removeAllViews(R.id.container)

            for (p in dayMenu.particulars) {
                val itemView = RemoteViews(context.packageName, R.layout.widget_particular_element)
                itemView.setTextViewText(R.id.foodType, p.type)
                itemView.setTextViewText(R.id.foodMenu, p.food)
                itemView.setTextViewText(R.id.foodTiming, p.time)
                views.addView(R.id.container, itemView)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getMenu(onResult: (DayMenu) -> Unit) {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        mess.getMainMenu { menu ->
            val dayMenu = menu.menu[day]
            onResult(dayMenu)
        }
    }
}
