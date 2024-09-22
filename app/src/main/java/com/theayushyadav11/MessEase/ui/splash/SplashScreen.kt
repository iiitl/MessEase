package com.theayushyadav11.MessEase.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.theayushyadav11.MessEase.MainActivity
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDatabase
import com.theayushyadav11.MessEase.ui.more.ErrorActivity
import com.theayushyadav11.MessEase.ui.more.UpdateActivity
import com.theayushyadav11.MessEase.ui.splash.fragments.LoginAndSignUpActivity
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Mess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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



        Handler().postDelayed({
            if (isFirstTime()) {
                getUpdate {
                    setMainMenu {
                        navigate()
                    }
                }
            } else navigate()

        }, 1500)

    }

    fun initialise() {
        mess = Mess(this)
    }

    private fun getUpdate(onResult: () -> Unit) {
        firestoreReference.collection("Update").document("update")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    mess.setUpdate("", "")
                    onResult()
                    return@addSnapshotListener
                }
                val version = value?.getString("version")
                val url = value?.getString("url")
                if (version != null && url != null) {
                    mess.setUpdate(version, url)
                    onResult()
                } else {

                    mess.setUpdate("", "")
                    onResult()
                }

            }

    }

    private fun navigate() {
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        mess.getUpdates { version, _ ->
            mess.log(version)
            mess.log(versionName)
            if (version == "") {

                startActivity(Intent(this, ErrorActivity::class.java))
                finish()
            } else if (version != versionName) {
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

    private fun isFirstTime(): Boolean {
        if (mess.get("firstTime") == "") {
            mess.save("firstTime", "false")
            return true
        } else return false

    }

    private fun setMainMenu(onResult: () -> Unit) {
        firestoreReference.collection("MainMenu").document("menu")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onResult()
                    return@addSnapshotListener
                }
                val menu = (value?.toObject(Menu::class.java)!!)
                lifecycleScope.launch(Dispatchers.IO) {
                    val menuDatabase = MenuDatabase.getDatabase(this@SplashScreen).menuDao()
                    val newMenu = Menu(
                        id = 0, creator = menu.creator, menu = menu.menu
                    )
                    menuDatabase.addMenu(newMenu)
                    withContext(Dispatchers.Main) {
                        onResult()
                    }

                }
            }

    }


}
