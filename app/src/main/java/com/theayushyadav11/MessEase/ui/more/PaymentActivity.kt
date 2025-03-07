package com.theayushyadav11.MessEase.ui.more

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.theayushyadav11.MessEase.databinding.ActivityPaymentBinding
import com.theayushyadav11.MessEase.utils.Constants.Companion.RAZORPAY_API_KEY
import com.theayushyadav11.MessEase.utils.Mess
import org.json.JSONObject


class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private lateinit var binding: ActivityPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialise()
        listeners()

    }

    private fun listeners() {

        Checkout.preload(applicationContext)
        val co = Checkout()
        co.setKeyID(RAZORPAY_API_KEY)

        binding.btnPay.setOnClickListener {
            startPayment()
        }


    }

    private fun startPayment() {
        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name", "MessEase")
            options.put("description", "payment for the order")
            //You can omit the image option to fetch the image from the Dashboard
            options.put("image","https://github.com/user-attachments/assets/02c34e6a-2e85-4745-82b8-715d2fdda3df")
            options.put("theme.color", "#1972f0");
            options.put("currency", "INR");
            options.put("amount", "500")//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email", "theayushyadav11@gmail.com")
            prefill.put("contact", "9696620395")

            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    private fun initialise() {


        setUpToolBar()
    }

    fun setUpToolBar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Ensure that the support action bar is not null before setting the title
        supportActionBar?.apply {
            title = "Payments"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toast.makeText(this@PaymentActivity, "payment success", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this@PaymentActivity,p2.toString(), Toast.LENGTH_SHORT).show()
        Mess(this@PaymentActivity).log(p2.toString())
    }


//    private fun openGooglePayWithQR() {
//        try {
//            val intent = Intent(Intent.ACTION_VIEW)
//
//            intent.data = Uri.parse("upi://pay?pa=paytmqr1o2ycrqg1f@paytm&pn=Paytm")
//            startActivity(intent)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(this, "No payment apps present.", Toast.LENGTH_LONG).show()
//        }
//    }
}