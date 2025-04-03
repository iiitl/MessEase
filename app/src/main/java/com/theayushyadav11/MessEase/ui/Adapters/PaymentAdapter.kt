package com.theayushyadav11.MessEase.ui.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.Models.Payment
import com.theayushyadav11.MessEase.Models.PaymentStatus
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Constants.Companion.formatTimeMillis

class PaymentAdapter(val payments: List<Payment>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.payment_element, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val payment = payments[position]
        (holder as PaymentViewHolder).purpose.text = payment.purpose
        if (hasDecimalValue(payment.amount))
            holder.amount.text = "₹${payment.amount}"
        else
            holder.amount.text = "₹${payment.amount.toInt()}"

        holder.time.text = formatTimeMillis(payment.timeStamp)

        if(payment.status== PaymentStatus.SUCCESS) {
           // holder.amount.setTextColor(Color.parseColor("#228B22"))
            holder.statusImg.setImageResource(R.drawable.success_svgrepo_com)
            holder.statusMsg.text = "Success"
            holder.statusMsg.setTextColor(Color.parseColor("#228B22"))
            holder.amount.setTextColor(Color.GRAY)
        }
        else {
            holder.amount.setTextColor(Color.GRAY)
        }
    }

    override fun getItemCount() = payments.size
    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val purpose = itemView.findViewById<TextView>(R.id.tvPurpose)
        val amount = itemView.findViewById<TextView>(R.id.amount)
        val status=itemView.findViewById<LinearLayout>(R.id.status)
        val time = itemView.findViewById<TextView>(R.id.time)
        val statusImg=itemView.findViewById<ImageView>(R.id.status_img)
        val statusMsg=itemView.findViewById<TextView>(R.id.status_msg)
    }

    fun hasDecimalValue(amount: Double): Boolean {
        return amount % 1 != 0.0
    }

}