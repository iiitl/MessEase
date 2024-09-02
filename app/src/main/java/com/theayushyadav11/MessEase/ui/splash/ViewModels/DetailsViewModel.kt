package com.theayushyadav11.MessEase.ui.splash.ViewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import com.theayushyadav11.MessEase.Models.User

class DetailsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val databaeReference = FirebaseDatabase.getInstance().reference
    private val firestoreReference = Firebase.firestore


    fun addDetails(name: String, batch: String,passingyear:String,gender: String,
                   onSuccess: () -> Unit,
                   onFailure: (Exception) -> Unit){

        val displayName=auth.currentUser?.displayName?:name
        val user= User(
            uid = auth.currentUser?.uid.toString(),
            name = displayName,
            email = auth.currentUser?.email.toString(),
            batch = batch,
            gender = gender,
            passingYear = passingyear,
            photoUrl = auth.currentUser?.photoUrl.toString(),
        )
        firestoreReference.collection("Users").document(auth.currentUser?.uid.toString()).set(user).addOnCompleteListener{
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