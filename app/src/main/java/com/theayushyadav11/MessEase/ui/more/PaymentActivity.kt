package com.theayushyadav11.MessEase.ui.more

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.ActivityPaymentBinding

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
        findViewById<Button>(R.id.btnPay).setOnClickListener {
            openGooglePayWithQR()
        }
    }

    private fun initialise() {
        try {
            Checkout.preload(applicationContext)
            val co = Checkout()

            co.setKeyID("IHhEavcmQQdYClbIvaXgvdEt")
        } catch (e: Exception) {

        }

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
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show()
    }
    private fun openGooglePayWithQR() {
        try {
               val intent = Intent(Intent.ACTION_VIEW)

                intent.data = Uri.parse("upi://pay?pa=paytmqr1o2ycrqg1f@paytm&pn=Paytm")
                startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "No payment apps present.", Toast.LENGTH_LONG).show()
        }
    }
}