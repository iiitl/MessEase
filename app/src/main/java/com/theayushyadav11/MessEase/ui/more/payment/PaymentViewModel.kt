package com.theayushyadav11.MessEase.ui.more.payment

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.razorpay.Checkout
import com.theayushyadav11.MessEase.Models.Payment
import com.theayushyadav11.MessEase.Models.PaymentStatus
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Constants.Companion.LOGO_LINK
import com.theayushyadav11.MessEase.utils.Constants.Companion.PAYMENTS
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.getKey
import org.json.JSONObject

class PaymentViewModel : ViewModel() {

    val amount = MutableLiveData<Double>(0.0)
    val purpose = MutableLiveData<String>("")

    fun startPayment(context: Activity, user: User) {

        val activity: Activity = context
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name", "MessEase")
            options.put("description", purpose.value)
            options.put(
                "image",
                LOGO_LINK
            )
            options.put("theme.color", ContextCompat.getColor(context, R.color.food));
            options.put("currency", "INR");
            options.put("amount", amount.value?.times(100))

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("contact", "9696620395")
            options.put("email", user.email)

            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    fun onPaymentComplete(amount: Double?, purpose: String?, user: User, status: PaymentStatus) {
        try {
            val payment = Payment(
                id = getKey(),
                uid = user.uid,
                amount = amount!!,
                purpose = purpose!!,
                timeStamp = System.currentTimeMillis(),
                name = user.name,
                email = user.email,
                status = status
            )
            firestoreReference.collection(PAYMENTS).document(payment.id).set(payment)
        } catch (e: Exception) {
            Log.d("PaymentViewModel", "startPayment: ${e.message}")
        }
    }
}