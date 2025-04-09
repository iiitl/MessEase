package com.theayushyadav11.MessEase.ui.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.Models.SpecialMeal
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Constants.Companion.SPECIAL_MEAL
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.formatTimeMillis
import com.theayushyadav11.MessEase.utils.Mess

class SpecialMealAdapter(val meals: List<SpecialMeal>,val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.special_meal_history_element, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val meal = meals[position]
        (holder as PaymentViewHolder)
        when (meal.mealIndex) {
            0 -> {
                holder.type.text = "Breakfast"
            }

            1 -> {
                holder.type.text = "Lunch"
            }

            2 -> {
                holder.type.text = "Snacks"
            }

            3 -> {
                holder.type.text = "Dinner"
            }

            else -> {
                holder.type.text = "Unknown"
            }
        }
        holder.menu.text = meal.food
        holder.date.text = "${meal.day}/${meal.month}/${1900+meal.year}"
        holder.delete.setOnClickListener {
            Mess(context).showAlertDialog(
                "Delete Meal",
                "Are you sure you want to delete this meal?",
                "Yes",
                "No"
            ) {
                deleteMeal(position)
            }
        }


    }

    override fun getItemCount() = meals.size
    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val type = itemView.findViewById<TextView>(R.id.foodType)
        val menu = itemView.findViewById<TextView>(R.id.foodMenu)
        val date = itemView.findViewById<TextView>(R.id.date)
        val delete = itemView.findViewById<ImageButton>(R.id.delete)

    }

    fun deleteMeal(position: Int) {
        meals.toMutableList().removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, meals.size)
        firestoreReference.collection(SPECIAL_MEAL).document(meals[position].timestamp.toString()).delete()
    }
}