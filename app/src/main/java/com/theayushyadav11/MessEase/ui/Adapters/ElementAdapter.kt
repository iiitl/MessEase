package com.theayushyadav11.MessEase.ui.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.Models.Element
import com.theayushyadav11.MessEase.R

class ElementAdapter(
    private val elements:List<Element>
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return (elements[position].type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
     return when(viewType){
         0->{
              val view = LayoutInflater.from(parent.context).inflate(R.layout.particulars_element_layout, parent, false)
              ParticularsViewHolder(view)
         }
         1->{
             val view = LayoutInflater.from(parent.context).inflate(R.layout.poll_element_layout, parent, false)
             PollViewHolder(view)
         }
         2->{
             val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_element_layout, parent, false)
             MsgViewHolder(view)
         }

         else -> {
             throw IllegalArgumentException("Invalid view type")
         }
     }
    }



    override fun getItemCount(): Int {
        return elements.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val element = elements[position]
        when (holder) {
            is ParticularsViewHolder -> holder.bind(element)
            is PollViewHolder -> holder.bind(element)
            is MsgViewHolder -> holder.bind(element)
        }
    }





    inner class ParticularsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Element ) {
            val particulars=item.particulars
            val type=itemView.findViewById<TextView>(R.id.foodType)
            val food=itemView.findViewById<TextView>(R.id.foodMenu)
            val time=itemView.findViewById<TextView>(R.id.foodTimeing)
            type.text=particulars.type
            food.text=particulars.food
            time.text=particulars.time

        }
    }
    inner class PollViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Element ) {
            val poll=item.poll
            val adder=itemView.findViewById<ViewGroup>(R.id.radioGroup)
            val question=itemView.findViewById<TextView>(R.id.tvQuestion)
            val date=itemView.findViewById<TextView>(R.id.date)
            val time=itemView.findViewById<TextView>(R.id.time)
            val name=itemView.findViewById<TextView>(R.id.tvname)
            val dIcon=itemView.findViewById<ImageView>(R.id.dIcon)
            itemView.findViewById<LinearLayout>(R.id.vv).visibility=View.GONE
            itemView.findViewById<ImageView>(R.id.delete).visibility=View.GONE
            question.text=poll.question
            date.text=poll.date
            time.text=poll.time






        }
    }
    inner class MsgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Element ) {
            val particulars=item.msg

        }
    }


}