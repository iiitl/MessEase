package com.theayushyadav11.MessEase.ui.more.payment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.theayushyadav11.MessEase.Models.PaymentStatus
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.ActivityPaymentBinding
import com.theayushyadav11.MessEase.utils.Mess


class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var mess: Mess
    private val paymentViewModel: PaymentViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        listeners()

    }

    private fun initialise() {
        setUpToolBar()
        amountWatcher()
        mess = Mess(this)
        binding.btnPay.isFocusable = true
    }

    private fun listeners() {
        Checkout.preload(applicationContext)
        val co = Checkout()
        co.setKeyID(getString(R.string.razorpay_key))

        binding.btnPay.setOnClickListener {
            if (binding.etAmount.text.toString().isEmpty()) {
                mess.toast("Please enter amount!")
            } else if (binding.etPurpose.text.toString().isEmpty()) {
                mess.toast("Please enter purpose!")
            } else if (binding.etAmount.text.toString().toDouble() * 100 <= 0) {
                mess.toast("Please enter valid amount!")
            } else {
                paymentViewModel.amount.value = binding.etAmount.text.toString().toDouble()
                paymentViewModel.purpose.value = binding.etPurpose.text.toString()
                paymentViewModel.startPayment(this@PaymentActivity, mess.getUser())
            }
        }


    }


    private fun amountWatcher() {
        binding.etAmount.doOnTextChanged { text, start, before, count ->

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
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val historyIcon: ImageButton = toolbar.findViewById(R.id.history_icon)
        historyIcon.setOnClickListener {
            startActivity(Intent(this, PaymentHistory::class.java))
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        paymentViewModel.onPaymentComplete(
            paymentViewModel.amount.value,
            paymentViewModel.purpose.value,
            mess.getUser(),
            PaymentStatus.SUCCESS
        )
        binding.etAmount.text?.clear()
        binding.etPurpose.text?.clear()
        mess.toast("Payment Successful!")
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        paymentViewModel.onPaymentComplete(
            paymentViewModel.amount.value,
            paymentViewModel.purpose.value,
            mess.getUser(),
            PaymentStatus.FAILED
        )
        binding.etAmount.text?.clear()
        binding.etPurpose.text?.clear()
        mess.toast("Payment Failed!")
    }

}


