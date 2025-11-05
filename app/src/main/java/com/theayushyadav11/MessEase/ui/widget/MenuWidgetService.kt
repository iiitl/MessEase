package com.theayushyadav11.MessEase.ui.widget

import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.theayushyadav11.MessEase.Models.DayMenu
import com.theayushyadav11.MessEase.Models.Particulars
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDatabase
import com.theayushyadav11.MessEase.utils.Mess
import kotlinx.coroutines.runBlocking

class MenuWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return MenuRemoteViewsFactory(applicationContext)
    }
}

class MenuRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private var particulars = emptyList<Particulars>()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        runBlocking {
            val menuDao = MenuDatabase.getDatabase(context).menuDao()
            particulars = menuDao.getMenu().menu[day].particulars
        }
    }

    override fun onDestroy() {
        particulars = emptyList()
    }

    override fun getCount(): Int = particulars.size

    override fun getViewAt(position: Int): RemoteViews {
        val item = particulars[position]
        val rv = RemoteViews(context.packageName, R.layout.widget_particular_element)

        rv.setTextViewText(R.id.foodType, item.type)
        rv.setTextViewText(R.id.foodMenu, item.food.trim().replace("\n", " "))
        rv.setTextViewText(R.id.foodTiming, item.time)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
}
