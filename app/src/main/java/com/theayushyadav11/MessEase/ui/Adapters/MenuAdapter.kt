package com.theayushyadav11.MessEase.ui.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.Models.Particulars
import com.theayushyadav11.MessEase.R

class MenuAdapter(
    private val particulars:List<Particulars>,
  ) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.particulars_element_layout, parent, false)
        return ParticularsViewHolder(view)
    }

    override fun getItemCount()=4

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val particular = particulars[position]
        when (holder) {
            is ParticularsViewHolder -> holder.bind(particular)
        }
    }
    inner class ParticularsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(particular: Particulars) {
            itemView.findViewById<TextView>(R.id.foodType).text=particular.type
            itemView.findViewById<TextView>(R.id.foodMenu).text=particular.food
            itemView.findViewById<TextView>(R.id.foodTimeing).text=particular.time

        }
    }
}