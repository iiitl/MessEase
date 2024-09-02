package com.theayushyadav11.MessEase.ui.NavigationDrawers.ViewModels

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.utils.Constants.Companion.databaseReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference

class AdminViewModel : ViewModel() {



    fun getDesList(onResult:(List<String>)->Unit)
    {
       databaseReference.child("Des").get().addOnSuccessListener {
              val list = mutableListOf<String>()
              for (i in it.children)
              {
                list.add(i.value.toString())
              }
              onResult(list)
       }
    }
    fun addToMessCommittee(email:String,designation:String,onResult: (String) -> Unit)
    {
       firestoreReference.collection("Users").whereEqualTo("email", email).get().addOnSuccessListener {
           if (it.isEmpty)
           {
               onResult("User not found")
           }
           else
           {
               val id = it.documents[0].id
               val updates = mapOf(
                   "designation" to designation,
                   "member" to true
               )

               firestoreReference.collection("Users").document(id).update(updates).addOnSuccessListener {
                   onResult("User added to committee")
               }
           }
       }
    }
}