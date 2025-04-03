package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.theayushyadav11.MessEase.Models.Msg
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.utils.Constants.Companion.COMPARER
import com.theayushyadav11.MessEase.utils.Constants.Companion.COORDINATOR
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESCENDING_ORDER
import com.theayushyadav11.MessEase.utils.Constants.Companion.DEVELOPER
import com.theayushyadav11.MessEase.utils.Constants.Companion.MESSAGES
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference

class MsgViewModel : ViewModel() {
    private var listenerRegistration: ListenerRegistration? = null

    fun getMyMsgs(uid: String, user: User, onResult: (List<Msg>) -> Unit) {
        removeSnapshotListener()

        listenerRegistration = firestoreReference.collection(MESSAGES)
            .orderBy(COMPARER, DESCENDING_ORDER)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    onResult(listOf())
                    return@addSnapshotListener
                }
                val msgs = mutableListOf<Msg>()
                value?.documents?.forEach {
                    val msg = it.toObject(Msg::class.java)
                    if (msg != null) {
                        if (msg.creater.uid == uid || user.designation == COORDINATOR || user.designation == DEVELOPER) {
                            msgs.add(msg)
                        }
                    }
                }
                onResult(msgs)

            }
    }

    private fun removeSnapshotListener() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    override fun onCleared() {
        super.onCleared()
        removeSnapshotListener()
    }
}
