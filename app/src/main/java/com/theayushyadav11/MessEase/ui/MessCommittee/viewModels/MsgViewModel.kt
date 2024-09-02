package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Msg
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESCENDING_ORDER
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference

class MsgViewModel : ViewModel() {
    fun getMyMsgs(uid: String, onResult: (List<Msg>) -> Unit) {
        firestoreReference.collection("Msgs").orderBy("comp", DESCENDING_ORDER)
            .addSnapshotListener { value, error ->
                fireBase.getUser(uid, onSuccess = {user->
                    if (error != null) {
                        onResult(listOf())
                        return@getUser
                    }
                    val msgs = mutableListOf<Msg>()
                    value?.documents?.forEach {
                        val msg = it.toObject(Msg::class.java)
                        if (msg != null) {
                            if (msg.creater.uid == uid||user.designation=="Coordinator"||user.designation=="Developer")

                                msgs.add(msg)
                        }
                    }
                    onResult(msgs)
                }, onFailure = {
                    onResult(listOf())
                })


            }
    }
}