package com.theayushyadav11.MessEase.ui.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.Models.Comment
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Mess

class CommentAdapter(private val comments: List<Comment>, context: Context, val mid: String) :
    RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {
    val mess = Mess(context)

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo = itemView.findViewById<ImageView>(R.id.profile)
        val time = itemView.findViewById<TextView>(R.id.time)
        val name = itemView.findViewById<TextView>(R.id.name)
        val tvcomment = itemView.findViewById<TextView>(R.id.comment)
        val delete = itemView.findViewById<ImageView>(R.id.delete)
        val email= itemView.findViewById<TextView>(R.id.email)
        val dIcon = itemView.findViewById<ImageView>(R.id.dIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.comment_list_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val comment = comments[position]
        mess.loadCircularImage(comment.creator.photoUrl, holder.photo)
        holder.time.text = comment.time
        holder.tvcomment.text = comment.comment
        holder.name.text = comment.creator.name
        holder.email.text=comment.creator.email
        if (comment.creator.member) {
            holder.dIcon.setImageResource(fireBase.getIcon(comment.creator.designation))
        } else holder.dIcon.visibility = View.GONE
             val it=mess.getUser()
            if (!ifdelete(comment, it.member)) {
                holder.delete.visibility = View.INVISIBLE
            }

        holder.delete.setOnClickListener {
            mess.showAlertDialog("Alert!", "Do you want to delete this comment?", "Yes", "No") {
                deleteComment(position)
            }

        }


    }

    fun ifdelete(comment: Comment, isMember: Boolean): Boolean {
        return (auth.currentUser?.uid == comment.creator.uid || isMember)
    }

    fun deleteComment(position: Int) {
        firestoreReference.collection("Msgs").document(mid).collection("Comments")
            .document(comments[position].id).delete().addOnSuccessListener {
                comments.toMutableList().removeAt(position)
                mess.toast("Comment deleted")
                notifyDataSetChanged()
            }.addOnFailureListener {
                mess.toast("Failed to delete comment")
            }


    }


    override fun getItemCount() = comments.size
}
