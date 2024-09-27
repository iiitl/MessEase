package com.theayushyadav11.MessEase.ui.more

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.ActivityPaymentBinding
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest


class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialise()
        listeners()

    }

    private fun listeners() {

    }

    private fun initialise() {
        PhonePe.init(this)
        val data: JSONObject = JSONObject()
        data.put("merchantTransactionId", System.currentTimeMillis().toString())
        data.put("merchantId", "MERCHANTUAT")
        data.put("merchantUserId", System.currentTimeMillis().toString())
        data.put("amount", 200)
        data.put("mobileNumber", "9696620395")
        data.put("callbackUrl", "123")
        val mPaymentInstrument = JSONObject()
        mPaymentInstrument.put("type", "PAY_PAGE")
        data.put("paymentInstrument", mPaymentInstrument)
        val base64Body: String =
            Base64.encodeToString(
                data.toString().toByteArray(
                    Charset.defaultCharset()
                ), Base64.NO_WRAP
            )

          val checksum=sha256("$base64Body/pg/v1/pay099eb0cd-02cf-4e2a-8aca-3e6c6aff0399")+"###1"
        val b2BPGRequest = B2BPGRequestBuilder()
            .setData(base64Body)
            .setChecksum(checksum)
            .setUrl("/pg/v1/pay")
            .build()
        findViewById<Button>(R.id.btnPay).setOnClickListener {
            try {
                startActivityForResult(PhonePe.getImplicitIntent(
                    /* Context */ this@PaymentActivity, b2BPGRequest, "")!!,1);
            } catch( e: PhonePeInitException){
                e.printStackTrace()
            }
        }



















        try {
            Checkout.preload(applicationContext)
            val co = Checkout()

            co.setKeyID("IHhEavcmQQdYClbIvaXgvdEt")
        } catch (e: Exception) {

        }

        setUpToolBar()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            Toast.makeText(this, "vjsvjsdjhvsjhvbcsjhvbs", Toast.LENGTH_SHORT).show()
            /*This callback indicates only about completion of UI flow.
                   Inform your server to make the transaction
                   status call to get the status. Update your app with the
                   success/failure status.*/
        }
    }
    private fun sha256(input: String): String {

        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")

        val digest = md.digest(bytes)

        return digest.fold("") { str, it -> str + "%02x".format(it) }
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