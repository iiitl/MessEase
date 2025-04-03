package com.theayushyadav11.MessEase.ui.more.payment

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Payment
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.utils.Constants.Companion.PAYMENTS
import com.theayushyadav11.MessEase.utils.Constants.Companion.UID
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PaymentHistoryViewModel : ViewModel() {


    fun getMyPayments(user: User, onResult: (List<Payment>) -> Unit) =
        CoroutineScope(Dispatchers.IO).launch {
            var payments = firestoreReference.collection(PAYMENTS).whereEqualTo(UID, user.uid)
                .get()
                .await()
                .toObjects(Payment::class.java)

            withContext(Dispatchers.Main) {
                payments = payments.sortedByDescending { it.timeStamp }
                onResult(payments)
            }
        }
}