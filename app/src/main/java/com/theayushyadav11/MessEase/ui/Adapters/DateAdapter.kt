package com.theayushyadav11.MessEase.ui.Adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.R
import java.util.Calendar
import java.util.Locale

class DateAdapter(
    private val listener: Listeners
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    var dates: MutableList<DateItem> = mutableListOf()
    var datOfWeek: Int = 0
    private var selectedPosition = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1

    interface Listeners {
        fun ondateSelected(date: DateItem, position: Int, main: DateViewHolder)


    }

    inner class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val day: TextView = view.findViewById(R.id.day)
        val dates: TextView = view.findViewById(R.id.date)
        val main: ConstraintLayout = view.findViewById(R.id.main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dateitem, parent, false)
        return DateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val yearMonth = java.time.YearMonth.now()
            yearMonth.lengthOfMonth()
        } else {
            val calendar = java.util.Calendar.getInstance()
            calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        }
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (dayOfMonth in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            datOfWeek = calendar.get(Calendar.DAY_OF_WEEK)


            val dayOfWeek =
                calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())

            val dateItem = DateItem(dayOfWeek.substring(0, 3), dayOfMonth, datOfWeek)
            dates.add(dateItem)
        }

        holder.day.text = dates[position].dayOfWeek.uppercase()
        holder.dates.text = dates[position].dayOfMonth.toString()
        if (position == selectedPosition) {

            holder.main.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.menu
                )
            )
            holder.dates.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.food))
        } else {
            holder.main.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.bg
                )
            )
            holder.dates.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.black
                )
            )
        }


        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            // Notify adapter to refresh the UI
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            listener.ondateSelected(dates[position], position, holder)
        }
    }

    private fun getCurrentDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK)
    }
}

data class DateItem(val dayOfWeek: String, val dayOfMonth: Int, val weekday: Int)
