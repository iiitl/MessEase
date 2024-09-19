package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels


import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Msg
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.utils.Constants.Companion.MESSAGES
import com.theayushyadav11.MessEase.utils.Constants.Companion.databaseReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.getCurrentDate
import com.theayushyadav11.MessEase.utils.Constants.Companion.getCurrentTimeInAmPm

class CreateMsgViewModel : ViewModel() {
    val listOfImages = MutableLiveData<MutableList<Uri>>()
    fun addMsg(
        title: String,
        body: String,
        user: User,
        photos: MutableList<ByteArray>,
        target: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {


        addPhotos(photos) { urls ->
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
            firestoreReference.collection(MESSAGES).document(id).set(msg).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }
        }

    }

    private fun addPhotos(photos: List<ByteArray>, onResult: (List<String>) -> Unit) {
        val urls = mutableListOf<String>()
        if (photos.isEmpty()) {
            onResult(emptyList())
        }
        for (photo in photos) {
            val key = databaseReference.push().key.toString()
            fireBase.uploadphoto(photo, "MsgImages/$key", onSuccess = { url ->
                urls.add(url)
                if (photos.indexOf(photo) == photos.size - 1) {
                    onResult(urls)
                }
            }, onFailure = {
                onResult(emptyList())
            })
        }
    }
}