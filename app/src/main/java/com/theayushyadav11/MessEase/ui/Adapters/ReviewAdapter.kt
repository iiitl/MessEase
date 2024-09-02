package com.theayushyadav11.MessEase.ui.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.Models.Review
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Mess

class ReviewAdapter(
    private val reviews:List<Review>,
    private val context: Context
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val mess= Mess(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.review_element,parent,false)
        return ReviewViewHolder(view)
    }

    override fun getItemCount()=reviews.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val review=reviews[position]
        (holder as ReviewViewHolder).name.text=review.creater.name
        holder.review.text=review.review
        if(review.review.isEmpty()){
            holder.review.visibility=View.GONE
        }
        holder.rating.rating=review.rating
        holder.type.text=review.day+"-"+review.foodtype
        holder.email.text=review.creater.email
        holder.time.text=review.dateTime
        holder.delete.setOnClickListener {
           delete(review)
        }
    }

    private fun delete(review: Review) {
        mess.showAlertDialog("Confirm","Are you sure you want to delete this review?","Delete","Cancel"){
            mess.addPb("Deleting Review")
            deleteReview(review){
               mess.pbDismiss()
            }
        }
    }

    private fun deleteReview(review: Review, function: () -> Unit) {
                firestoreReference.collection("Reviews").document(review.id).delete().addOnSuccessListener {
                    mess.toast("Review Deleted")
                    function()
                }.addOnFailureListener {
                    mess.toast("Failed to delete review")
                    function()
                }
    }

    inner class ReviewViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name: TextView =itemView.findViewById(R.id.mname)
        val review:TextView=itemView.findViewById(R.id.review)
        val rating: RatingBar =itemView.findViewById(R.id.ratingBar)
        val email: TextView =itemView.findViewById(R.id.email)
        val type: TextView =itemView.findViewById(R.id.type)
        val delete:ImageView=itemView.findViewById(R.id.delete)
        val time:TextView=itemView.findViewById(R.id.time)
    }
}