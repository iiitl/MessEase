package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import androidx.lifecycle.ViewModel
import androidx.room.Query
import com.theayushyadav11.MessEase.Models.AprMenu
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESCENDING_ORDER
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference

class UploadMenuViewModel : ViewModel() {


    fun getAprMenus(onSuccess:(List<AprMenu>)->Unit,onFailure:()->Unit){

        firestoreReference.collection("MenuForApproval").orderBy("date", DESCENDING_ORDER).addSnapshotListener{
            value,error->
            if(error!=null){
                onFailure()
                return@addSnapshotListener
            }
            val aprMenus:MutableList<AprMenu> = mutableListOf()
            for(document in value!!){
                val aprMenu = document.toObject(AprMenu::class.java)
                aprMenus.add(aprMenu)
            }
            onSuccess(aprMenus)
        }
    }

}