package com.theayushyadav11.MessEase.ui.NavigationDrawers.ViewModels

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESIGNATION
import com.theayushyadav11.MessEase.utils.Constants.Companion.EMAIL
import com.theayushyadav11.MessEase.utils.Constants.Companion.IS_MEMBER
import com.theayushyadav11.MessEase.utils.Constants.Companion.USERS
import com.theayushyadav11.MessEase.utils.Constants.Companion.databaseReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference

class AdminViewModel : ViewModel() {


    fun addToMessCommittee(email: String, designation: String, onResult: (String) -> Unit) {
        firestoreReference.collection(USERS).whereEqualTo(EMAIL, email).get().addOnSuccessListener {
            if (it.isEmpty) {
                onResult("User not found")
            } else {
                val id = it.documents[0].id
                val updates = mapOf(
                    DESIGNATION to designation,
                    IS_MEMBER to true
                )

                firestoreReference.collection(USERS).document(id).update(updates)
                    .addOnSuccessListener {
                        onResult("User added to committee")
                    }
            }
        }
    }
}