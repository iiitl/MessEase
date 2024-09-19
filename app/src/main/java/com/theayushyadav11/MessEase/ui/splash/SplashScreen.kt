package com.theayushyadav11.MessEase.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.theayushyadav11.MessEase.MainActivity
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.ui.more.ErrorActivity
import com.theayushyadav11.MessEase.ui.more.UpdateActivity
import com.theayushyadav11.MessEase.ui.splash.fragments.LoginAndSignUpActivity
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Mess

class SplashScreen : AppCompatActivity() {

    private lateinit var mess: Mess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        initialise()
        val imageView = findViewById<ImageView>(R.id.imageViewLogo)
        val fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        imageView.startAnimation(fadeAnimation)
        mess.log(mess.getUser())
        Handler().postDelayed({
            navigate()

        }, 1000)

    }

    fun initialise() {
        mess = Mess(this)
    }

    private fun navigate() {
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        fireBase.getUpdates { version, url ->
//             if(version=="")
//             {
//                 startActivity(Intent(this, ErrorActivity::class.java))
//                 finish()
//             }
          if (version != versionName&&false) {
                mess.log(version)
                mess.log(versionName)
                startActivity(Intent(this, UpdateActivity::class.java))
                finish()

            } else {
                if (mess.isLoggedIn()) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, LoginAndSignUpActivity::class.java))
                    finish()
                }
            }


        }

    }
}
