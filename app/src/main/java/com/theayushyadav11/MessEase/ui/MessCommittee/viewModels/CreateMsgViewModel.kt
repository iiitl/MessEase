package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels


import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Msg
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.databaseReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.getCurrentDate
import com.theayushyadav11.MessEase.utils.Constants.Companion.getCurrentTimeInAmPm

class CreateMsgViewModel : ViewModel() {
   val listOfImages=MutableLiveData<MutableList<Uri>>()
    fun addMsg(
        title: String,
        body: String,
        photos: MutableList<ByteArray>,
        target: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {


            fireBase.getUser(auth.currentUser?.uid.toString(), onSuccess = { user ->
                addPhotos(photos){urls->
                    val id = databaseReference.push().key.toString()
                    val msg = Msg(
                        uid = id,
                        creater = user,
                        time = getCurrentTimeInAmPm(),
                        date = getCurrentDate(),
                        title = title,
                        body = body,
                        photos = urls,
                        target = target

                    )
                    firestoreReference.collection("Msgs").document(id).set(msg).addOnSuccessListener {
                        onSuccess()
                    }.addOnFailureListener {
                        onFailure()
                    }
                }
            }, onFailure = {
                onFailure()
            })

    }
    fun addPhotos(photos:List<ByteArray>,onResult:(List<String>)->Unit){
        val urls= mutableListOf<String>()
        if(photos.isEmpty()){
            onResult(emptyList())
        }
        for(photo in photos){
            val key=databaseReference.push().key.toString()
            fireBase.uploadphoto(photo,"MsgImages/$key",onSuccess = {url->
                urls.add(url)
               if(photos.indexOf(photo)==photos.size-1){
                   onResult(urls)
               }
            }, onFailure = {
                onResult(emptyList())
            })
        }
    }
}