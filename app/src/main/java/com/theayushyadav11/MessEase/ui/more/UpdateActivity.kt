package com.theayushyadav11.MessEase.ui.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.theayushyadav11.MessEase.MainActivity
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.ActivityUpdateBinding
import com.theayushyadav11.MessEase.ui.splash.fragments.LoginAndSignUpActivity
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.isVersionGreater
import com.theayushyadav11.MessEase.utils.Mess

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var mess: Mess
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mess = Mess(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnUpdate.setOnClickListener {
            fireBase.getUpdates { version, url ->
                val finalUrl = if (isVersionGreater(version, "1.2")) {
                    "https://play.google.com/store/apps/details?id=com.theayushyadav11.MessEase"
                } else {
                    url
                }
                
                if (finalUrl != "") {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(finalUrl)
                    startActivity(intent)
                }

            }
        }

        binding.btnSkip.setOnClickListener {

            if (mess.isLoggedIn()) {
                startActivity(Intent(this, MainActivity::class.java))
                mess.putUpdateSkipped(true)
                finish()
            } else {
                startActivity(Intent(this, LoginAndSignUpActivity::class.java))
                finish()
            }
        }
    }
}