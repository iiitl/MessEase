package com.theayushyadav11.MessEase.ui.more.payment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.theayushyadav11.MessEase.databinding.ActivityPaymentHistoryBinding
import com.theayushyadav11.MessEase.ui.Adapters.PaymentAdapter
import com.theayushyadav11.MessEase.utils.Mess

class PaymentHistory : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentHistoryBinding
    private val viewModel: PaymentHistoryViewModel by viewModels()
    private lateinit var mess: Mess
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        listeners()
    }

    private fun listeners() {
        binding.refreshLayout.setOnRefreshListener {
            getMyPayments()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun initialise() {
        mess = Mess(this)
        setUpToolBar()
        getMyPayments()
    }

    fun setUpToolBar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Payment History"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun getMyPayments() {
        mess.addPb("Loading Payments")
        viewModel.getMyPayments(mess.getUser()) {
            if (it.isNotEmpty()) {
               binding.message.visibility=View.GONE
                binding.recyclerView.isVisible = true
                binding.recyclerView.apply {
                    adapter = PaymentAdapter(it)
                    layoutManager = LinearLayoutManager(this@PaymentHistory)
                }
            }
            else
            {
                binding.message.isVisible=true
                binding.recyclerView.isVisible=false
            }
            mess.pbDismiss()
        }
    }

}