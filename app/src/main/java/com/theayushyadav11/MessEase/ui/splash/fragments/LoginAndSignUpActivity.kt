package com.theayushyadav11.MessEase.ui.splash.fragments

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.ActivityLoginAndSignUpBinding

class LoginAndSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginAndSignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginAndSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.host)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}