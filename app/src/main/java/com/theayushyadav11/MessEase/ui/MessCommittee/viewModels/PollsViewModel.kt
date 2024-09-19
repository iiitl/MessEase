package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Msg
import com.theayushyadav11.MessEase.Models.OptionSelected
import com.theayushyadav11.MessEase.Models.Poll
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.utils.Constants.Companion.COMPARER
import com.theayushyadav11.MessEase.utils.Constants.Companion.COORDINATOR
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESCENDING_ORDER
import com.theayushyadav11.MessEase.utils.Constants.Companion.DEVELOPER
import com.theayushyadav11.MessEase.utils.Constants.Companion.POLLS
import com.theayushyadav11.MessEase.utils.Constants.Companion.POLL_RESULT
import com.theayushyadav11.MessEase.utils.Constants.Companion.SELECTED_OPTION
import com.theayushyadav11.MessEase.utils.Constants.Companion.USERS
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference

class PollsViewModel : ViewModel() {

    fun getMyPolls(user: User,onResult:(List<Poll>)->Unit)
    {
        firestoreReference.collection(POLLS).orderBy(COMPARER, DESCENDING_ORDER).addSnapshotListener{
                value, error ->

                if (error != null) {
                    onResult(listOf())
                    return@addSnapshotListener
                }
                val polls = mutableListOf<Poll>()
                value?.documents?.forEach {
                    val poll = it.toObject(Poll::class.java)
                    if (poll != null) {
                        if (poll.creater.uid == user.uid||user.designation== COORDINATOR||user.designation== DEVELOPER)
                            polls.add(poll)
                    }
                }
                onResult(polls)
        }
    }
    fun getVotesOnOption(pid:String, option:String,onResult: (Int) -> Unit)
    {
        firestoreReference.collection(POLL_RESULT).document(pid).collection(USERS).whereEqualTo(
            SELECTED_OPTION,option).addSnapshotListener{
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