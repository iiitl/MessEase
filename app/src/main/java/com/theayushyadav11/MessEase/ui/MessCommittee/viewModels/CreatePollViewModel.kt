package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Poll
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
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val id = databaseReference.push().key.toString()
        fireBase.getUser(auth.currentUser?.uid.toString(),
            onSuccess = { user ->
                val poll = Poll(
                    id = id,
                    creater = user,
                    question = question,
                    date = getCurrentDate(),
                    time = getCurrentTimeInAmPm(),
                    options = options,
                    target = target
                )
                firestoreReference.collection("Polls").document(id).set(poll).addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener {
                    onFailure(it)
                }

            }, onFailure = {
                onFailure(it)

            })

    }

}