package com.theayushyadav11.MessEase.ui.more

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.theayushyadav11.MessEase.R
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
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name", "MessEase")
            options.put("description", "payment for the order")
            options.put(
                "image",
                "https://github.com/user-attachments/assets/02c34e6a-2e85-4745-82b8-715d2fdda3df"
            )
            options.put("theme.color", ContextCompat.getColor(this, R.color.food));
            options.put("currency", "INR");
            options.put("amount", binding.etAmount.text.toString().toDouble() * 100)

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
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
        amountWatcher()
        binding.btnPay.isFocusable = true
    }

    private fun amountWatcher() {
       binding.etAmount.doOnTextChanged {
              text, start, before, count ->
              if (text.toString().isNotEmpty()) {
                binding.btnPay.isEnabled = true
              } else {
                binding.btnPay.isEnabled = false
              }
       }
    }

    fun setUpToolBar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
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
        binding.etAmount.text?.clear()
        Toast.makeText(this@PaymentActivity, "Payment Success!", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        binding.etAmount.text?.clear()
        Toast.makeText(this@PaymentActivity,"Payment Failed!", Toast.LENGTH_SHORT).show()
        Mess(this@PaymentActivity).log(p2.toString())
    }

}