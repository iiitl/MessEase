package com.theayushyadav11.MessEase.ui.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.Models.Poll
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.ui.MessCommittee.fragments.PollsFragment
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Mess

class PollAdapter(
    private val polls: List<Poll>, val context: Context
) : RecyclerView.Adapter<PollAdapter.PollViewHolder>() {
    val mess = Mess(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.poll_element_layout, parent, false)
        return PollViewHolder(view)
    }

    override fun getItemCount() = polls.size+1

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        val poll = polls[position]
        holder.bind(poll)
        holder.delete.setOnClickListener {
            mess.showAlertDialog(
                "Alert!", "Are you sure you want to delete this poll?", "Delete", "Cancel"
            ) {
                deletePoll(poll)
            }
        }


    }

    fun getVotesOnOption(pid: String, option: String, onResult: (Int) -> Unit) {
        firestoreReference.collection("PollResult").document(pid).collection("Users")
            .whereEqualTo("selected", option).addSnapshotListener { value, error ->
                if (error != null) {
                    onResult(0)
                    return@addSnapshotListener
                }
                val votes = value?.size()
                onResult(votes!!)
            }
    }

    fun getTotalVotes(pid: String, onResult: (Int) -> Unit) {
        firestoreReference.collection("PollResult").document(pid).collection("Users")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onResult(0)
                    return@addSnapshotListener
                }
                val votes = value?.size()
                onResult(votes!!)
            }
    }

    private fun deletePoll(poll: Poll) {
        mess.addPb("Deleting poll...")
        firestoreReference.collection("Polls").document(poll.id).delete().addOnSuccessListener {


            firestoreReference.collection("PollResult").document(poll.id).delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        mess.pbDismiss()
                        mess.toast("Poll deleted successfully")
                    } else {
                        mess.pbDismiss()
                        mess.toast("Failed to delete poll")
                    }
                }
        }.addOnFailureListener {
            mess.pbDismiss()
            mess.toast("Failed to delete poll")
        }


    }

    inner class PollViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val delete = itemView.findViewById<ImageView>(R.id.delete)


        fun bind(poll: Poll) {
            itemView.findViewById<TextView>(R.id.tvQuestion).text = poll.question
            itemView.findViewById<TextView>(R.id.date).text = poll.date
            itemView.findViewById<TextView>(R.id.time).text = poll.time
            itemView.findViewById<TextView>(R.id.tvname).text = poll.creater.name
            itemView.findViewById<LinearLayout>(R.id.vv).setOnClickListener{
                mess.sendPollId(poll.id)
                itemView.findNavController().navigate(R.id.action_mcMainPage_to_viewVotesFragment)
            }

            itemView.findViewById<ImageView>(R.id.dIcon)
                .setImageResource(fireBase.getIcon(poll.creater.designation))
            val adder = itemView.findViewById<LinearLayout>(R.id.radioGroup)
            addOptions(poll.id, adder, poll.options)


        }


        private fun addOptions(id: String, adder: LinearLayout, options: MutableList<String>) {
            options.forEach {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.option_layout, adder, false)
                view.findViewById<TextView>(R.id.title).text = it
                view.findViewById<RadioButton>(R.id.rb).visibility = View.INVISIBLE



                getTotalVotes(id) { votes ->
                    getVotesOnOption(id, it) { vote ->
                        view.findViewById<TextView>(R.id.nop).text = vote.toString()
                        val v = if (votes == 0) 0 else (vote * 100) / votes
                        view.findViewById<ProgressBar>(R.id.ProgressBar).progress = v
                    }
                }

                itemView.findViewById<ViewGroup>(R.id.radioGroup).addView(view)
            }
        }
    }
}