package com.theayushyadav11.MessEase.ui.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.Models.Payment
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Constants.Companion.formatTimeMillis

class PaymentAdapter(val payments:List<Payment>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.payment_element,parent,false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val payment=payments[position]
        (holder as PaymentViewHolder).purpose.text=payment.purpose
        holder.amount.text=payment.amount.toString()
        holder.time.text=formatTimeMillis(payment.timeStamp)
    }

    override fun getItemCount()=payments.size
    inner class PaymentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val purpose=itemView.findViewById<TextView>(R.id.tvPurpose)
        val amount=itemView.findViewById<TextView>(R.id.amount)
       // val status=itemView.findViewById<TextView>(R.id.status)
        val time=itemView.findViewById<TextView>(R.id.time)
    }
}