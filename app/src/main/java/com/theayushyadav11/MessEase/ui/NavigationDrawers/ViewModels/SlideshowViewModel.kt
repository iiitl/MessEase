package com.theayushyadav11.MessEase.ui.NavigationDrawers.ViewModels

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESIGNATION
import com.theayushyadav11.MessEase.utils.Constants.Companion.IS_MEMBER
import com.theayushyadav11.MessEase.utils.Constants.Companion.USERS
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference


class SlideshowViewModel : ViewModel() {

    fun getCoord(onResult: (List<User>) -> Unit) {
        firestoreReference.collection(USERS)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val list = mutableListOf<User>()
                for (user in value!!) {
                    val u = user.toObject(User::class.java)
                    if (u.member)
                        list.add(u)
                }
                onResult(list)
            }
    }
    fun delete(uid:String,onResult: (String) -> Unit) {
        val updates= hashMapOf<String,Any>(
            DESIGNATION to "",
            IS_MEMBER to false
        )
        firestoreReference.collection(USERS).document(uid).update(updates).addOnSuccessListener {
            onResult("Removed from Mess Committee")
        }.addOnFailureListener {
            onResult("Failed to remove")
        }
    }
}