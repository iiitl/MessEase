package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Poll
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.utils.Constants.Companion.POLLS
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.databaseReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.getCurrentDate
import com.theayushyadav11.MessEase.utils.Constants.Companion.getCurrentTimeInAmPm

class CreatePollViewModel : ViewModel() {


    fun addPoll(
        question: String,
        target: String,
        options: MutableList<String>,
        user:User,
        onSuccess: () -> Unit,

        onFailure: (Exception) -> Unit
    ) {
        val id = databaseReference.push().key.toString()
                val poll = Poll(
                    id = id,
                    creater = user,
                    question = question,
                    date = getCurrentDate(),
                    time = getCurrentTimeInAmPm(),
                    options = options,
                    target = target
                )
                firestoreReference.collection(POLLS).document(id).set(poll).addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener {
                    onFailure(it)
                }

    }

}