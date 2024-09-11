package com.theayushyadav11.MessEase.ui.MessCommittee.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.ActivityMessCommitteeBinding

class MessCommitteeMain : AppCompatActivity() {
    private lateinit var binding: ActivityMessCommitteeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMessCommitteeBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.mc_host)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}