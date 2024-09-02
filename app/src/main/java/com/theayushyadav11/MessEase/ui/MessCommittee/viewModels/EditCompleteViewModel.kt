package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theayushyadav11.MessEase.Models.AprMenu
import com.theayushyadav11.MessEase.Models.DayMenu
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDao
import com.theayushyadav11.MessEase.ui.MessCommittee.activities.EditCompleteActivity
import com.theayushyadav11.MessEase.utils.Constants.Companion.TAG
import com.theayushyadav11.MessEase.utils.Constants.Companion.databaseReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.menuFileName
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditCompleteViewModel(private val dao: MenuDao) : ViewModel() {


    fun getEditedMenu(onResult:(Menu)->Unit) {
        viewModelScope.launch{
            dao.getEditedMenu().let {
                onResult(it)
            }
        }
    }
    fun sendToApprove(uri: Uri,note:String,menu:Menu,onSuccess:(String)->Unit,onFailure:(Exception)->Unit){
       fireBase.uploadfile(uri,"ApprovePdf/$menuFileName${databaseReference.push().key}",
           onSuccess={ url->
              val key=  databaseReference.push().key.toString()
               val aprmenu= AprMenu(
                   key,
                   note,
                   url,
                   Date(),
                   menu,
                   getCurrentTimeAndDate(),
                   getComp(menu)

               )
               firestoreReference.collection("MenuForApproval").document(key).set(aprmenu).addOnCompleteListener{
                   if(it.isSuccessful)
                   {
                          onSuccess(url)
                   }
                   else
                   {
                          onFailure(it.exception!!)
                   }
               }
           },
           onFailure = {
               onFailure(it)
           }
           )
    }
    fun ifSameExists(comp:String,onResult: (Boolean) -> Unit,onError: (String) -> Unit)
    {
        firestoreReference.collection("MenuForApproval").whereEqualTo("comp",comp).get().addOnCompleteListener {

          if(it.isSuccessful)
          {
              if(it.result!!.isEmpty)
              {
                  onResult(false)
              }
              else
              {
                  onResult(true)
              }
          }
          else
          {
              onError(it.exception!!.message!!)
          }





        }
    }
 fun getComp(menu: Menu): String {
        var c=""
        for(i in menu.menu)
        {
            for(j in i.particulars)
            {
                c+=j.food
            }
        }
        return c
    }
    private fun getCurrentTimeAndDate(): String {
        val dateFormat = SimpleDateFormat("hh:mm a dd MMM yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}