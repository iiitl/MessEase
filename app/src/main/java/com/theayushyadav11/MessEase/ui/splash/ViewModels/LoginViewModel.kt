package com.theayushyadav11.MessEase.ui.splash.ViewModels

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import com.theayushyadav11.MessEase.MainActivity
import com.theayushyadav11.MessEase.R

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val databaeReference = FirebaseDatabase.getInstance().reference
    private val firestoreReference = Firebase.firestore


    fun isPresent(onSuccess:(Boolean)->Unit){
        firestoreReference.collection("Users").document(auth.currentUser?.uid.toString()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result?.exists() == true) {
                        onSuccess(true)
                    } else {
                        onSuccess(false)
                    }
                } else {
                    onSuccess(false)
                }
            }
    }
}