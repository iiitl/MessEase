package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Msg
import com.theayushyadav11.MessEase.Models.OptionSelected
import com.theayushyadav11.MessEase.Models.Poll
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESCENDING_ORDER
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference

class PollsViewModel : ViewModel() {

    fun getMyPolls(uid:String,onResult:(List<Poll>)->Unit)
    {
        firestoreReference.collection("Polls").orderBy("comp", DESCENDING_ORDER).addSnapshotListener{
                value, error ->
            fireBase.getUser(uid, onSuccess = { user->
                if (error != null) {
                    onResult(listOf())
                    return@getUser
                }
                val polls = mutableListOf<Poll>()
                value?.documents?.forEach {
                    val poll = it.toObject(Poll::class.java)
                    if (poll != null) {
                        if (poll.creater.uid == uid||user.designation=="Coordinator"||user.designation=="Developer")

                            polls.add(poll)
                    }
                }
                onResult(polls)
            }, onFailure = {
                onResult(listOf())
            })
        }
    }
    fun getVotesOnOption(pid:String, option:String,onResult: (Int) -> Unit)
    {
        firestoreReference.collection("PollResult").document(pid).collection("Users").whereEqualTo("selected",option).addSnapshotListener{
                value, error ->
            if(error!=null)
            {
                onResult(0)
                return@addSnapshotListener
            }
            val votes = value?.size()
            onResult(votes!!)
        }
    }

}