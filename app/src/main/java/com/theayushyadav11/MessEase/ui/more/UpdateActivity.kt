package com.theayushyadav11.MessEase.ui.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.isVersionGreater

class UpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialButton>(R.id.btnUpdate).setOnClickListener {
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
    }
}