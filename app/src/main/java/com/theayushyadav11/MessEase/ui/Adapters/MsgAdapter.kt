package com.theayushyadav11.MessEase.ui.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.DocumentReference
import com.theayushyadav11.MessEase.Models.Msg
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Mess

class MsgAdapter(
    val msgs: List<Msg>,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val mess = Mess(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.msg_element_layout, parent, false)
        return MsgViewHolder(view)
    }

    override fun getItemCount() = msgs.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = msgs[position]
        (holder as MsgViewHolder).bind(msg)

    }

    inner class MsgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private fun delete(msg: Msg) {
            mess.addPb("Deleting msg...")
            val ref=firestoreReference.collection("Msgs").document(msg.uid)
           ref.delete().addOnCompleteListener {
                if (it.isSuccessful) {
                    if(msg.photos.isEmpty()){
                        mess.pbDismiss()
                        mess.toast("Message deleted successfully")
                        fireBase.deleteSubcollections(ref,"Comments")
                        return@addOnCompleteListener
                    }
                    for (photo in msg.photos) {
                        fireBase.deletefile(photo, onSuccess = {

                            mess.pbDismiss()
                            mess.toast("Message deleted successfully")

                        },
                            onFailure = {
                                mess.pbDismiss()
                                mess.toast("Message deleted successfully")
                            })
                    }

                } else {
                    mess.pbDismiss()
                    mess.toast(it.exception?.message.toString())
                }
            }
        }


        fun bind(msg: Msg) {
            itemView.findViewById<TextView>(R.id.title).text = msg.title
            itemView.findViewById<TextView>(R.id.body).text = msg.body
            itemView.findViewById<ImageView>(R.id.dIcon)
                .setImageResource(fireBase.getIcon(msg.creater.designation))
            itemView.findViewById<TextView>(R.id.date).text = msg.date
            itemView.findViewById<TextView>(R.id.time).text = msg.time
            itemView.findViewById<TextView>(R.id.creater).text = msg.creater.name
            val delete = itemView.findViewById<ImageView>(R.id.delete)
            val comments = itemView.findViewById<LinearLayout>(R.id.comments)
            comments.visibility=View.GONE
            delete.visibility = View.VISIBLE
            val adder = itemView.findViewById<ViewGroup>(R.id.adder)
            for (photo in msg.photos) {
                val view = LayoutInflater.from(context).inflate(R.layout.img, adder, false)
                val img = view.findViewById<ImageView>(R.id.img)
                mess.loadImage(photo, img)
                img.setOnClickListener {
                    mess.showImage(photo)
                }
                adder.addView(view)
            }
            comments.setOnClickListener {
                openComments()
            }

            delete.setOnClickListener {
                mess.showAlertDialog(
                    "Alert!",
                    "Are you sure you want to delete this message?",
                    "Yes",
                    "No"
                ) {
                    delete(msg)
                }
            }

        }

    }

    private fun openComments() {
        showBottomSheetDialog()
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.comments_layout, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        val dismiss=bottomSheetDialog.findViewById<ImageView>(R.id.dismiss)
        dismiss?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

}