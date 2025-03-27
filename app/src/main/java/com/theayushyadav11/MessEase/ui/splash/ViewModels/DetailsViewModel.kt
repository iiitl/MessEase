package com.theayushyadav11.MessEase.ui.splash.ViewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.utils.Constants.Companion.COORDINATOR
import com.theayushyadav11.MessEase.utils.Constants.Companion.USERS
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference

class DetailsViewModel : ViewModel() {
    fun addDetails(name: String, batch: String,passingyear:String,gender: String,
                   onSuccess: () -> Unit,
                   onFailure: (Exception) -> Unit){

        val displayName=auth.currentUser?.displayName?:name
        val user= User(
            uid = auth.currentUser?.uid.toString(),
            name = displayName,
            email = auth.currentUser?.email.toString(),
            batch = batch,
            member = true,
            designation = COORDINATOR,
            gender = gender,
            passingYear = passingyear,
            photoUrl = auth.currentUser?.photoUrl.toString(),
        )
        firestoreReference.collection(USERS).document(auth.currentUser?.uid.toString()).set(user).addOnCompleteListener{
            if(it.isSuccessful){
               onSuccess()
            }
            else
            {
                onFailure(it.exception!!)
            }
        }

    }

}