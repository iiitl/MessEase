package com.theayushyadav11.MessEase


import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.theayushyadav11.MessEase.databinding.ActivityMainBinding
import com.theayushyadav11.MessEase.notifications.AlarmReceiver
import com.theayushyadav11.MessEase.ui.MessCommittee.activities.MessCommitteeMain
import com.theayushyadav11.MessEase.ui.more.PaymentActivity
import com.theayushyadav11.MessEase.ui.more.ReviewActivity
import com.theayushyadav11.MessEase.ui.more.SettingsActivity
import com.theayushyadav11.MessEase.ui.splash.fragments.LoginAndSignUpActivity
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.FireBase
import com.theayushyadav11.MessEase.utils.Mess
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mess: Mess
    private lateinit var alarmManager: AlarmManager
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1
    private val REQUEST_CODE_SCHEDULE_EXACT_ALARM = 2
    private lateinit var analytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        setUpNav()
        setUpHeader()
        createNotificationChannel()
        askForNotificationPermission()
        fireBase.getToken()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initialise() {
        mess = Mess(this)
        mess.setIsLoggedIn(true)
        analytics = FirebaseAnalytics.getInstance(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }

            R.id.action_payment -> {
                startActivity(Intent(this, PaymentActivity::class.java))
                true
            }
            R.id.action_review -> {
                startActivity(Intent(this,ReviewActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setDrawerClickListener(
        navView: NavigationView, drawerLayout: DrawerLayout, navController: NavController
    ) {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    firestoreReference.collection("Users")
                        .document(auth.currentUser?.uid.toString()).update("token", "")
                    signOut()
                    val intent = Intent(this@MainActivity, LoginAndSignUpActivity::class.java)
                    mess.setIsLoggedIn(false)
                    startActivity(intent)
                    finish()

                    true
                }

                R.id.nav_messCommitteeActivity -> {
                    FireBase().getUser(auth.currentUser!!.uid, onSuccess = { user ->
                        if (user.member&&auth.currentUser!=null) {
                            val intent = Intent(this, MessCommitteeMain::class.java)
                            startActivity(intent)
                        } else {


                            if (!isFinishing && !isDestroyed) {
                                mess.showAlertDialog(
                                    "Alert!",
                                    "You are not a member of Mess Committee!",
                                    "Ok",
                                    ""
                                ) {}
                            }
                        }
                    }, onFailure = {
                        Mess(this).toast(it)
                    })
                    true
                }

                R.id.nav_download -> {

                    firestoreReference.collection("MainMenu").document("url").get()
                        .addOnSuccessListener { value ->
                            val url = value?.get("url").toString()
                            val uri = (Uri.parse(url))
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)

                        }.addOnFailureListener { error ->
                            mess.toast("Failed to download menu")
                        }
                    true
                }

                else -> {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    private fun setUpNav() {
        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_messCommitteeActivity, R.id.nav_admin, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setDrawerClickListener(navView, drawerLayout, navController)

        val menu: Menu = navView.menu
        val admin = menu.findItem(R.id.nav_admin)
        fireBase.getUser(auth.currentUser?.uid.toString(), onSuccess = { user ->
            if (!(user.designation == "Coordinator" || user.designation == "Developer")) {
                admin.isVisible = false
            }
        }, onFailure = {

        })
    }

    private fun setUpHeader() {
        val headerView: View = binding.navView.getHeaderView(0)
        val layout: LinearLayout = headerView.findViewById(R.id.navMain)
        FireBase().getUser(auth.currentUser?.uid.toString(), onSuccess = { user ->
            if (!isFinishing && !isDestroyed) {
                mess.loadCircleImage(user.photoUrl, layout.findViewById<ImageView>(R.id.propic))
                layout.findViewById<TextView>(R.id.mname).text = user.name
                layout.findViewById<TextView>(R.id.email).text = user.email
            }


        }, onFailure = {
            Mess(this).toast(it)
        })


    }

    private fun signOut() {
        var mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()


        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "getString(R.string.channel_name)"
            val descriptionText = "getString(R.string.channel_description)"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("DailyNotification", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            cancelAllAlarms(this@MainActivity)
            setAlarm()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRestart() {
        super.onRestart()
        try {
            cancelAllAlarms(this@MainActivity)
            setAlarm()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this@MainActivity, AlarmReceiver::class.java)

        val times = listOf(
            mess.get("bt", "7:30"),
            mess.get("lt", "12:00"),
            mess.get("st", "16:30"),
            mess.get("dt", "19:00")
        )
        //val times = listOf("20:14","20:15","20:11","20:12")
        mess.log("Ayush"+
            mess.get("bt", "7:30") + mess.get("lt", "12:00") + mess.get(
                "st", "16:30"
            ) + mess.get("dt", "19:00")
        )
        for (i in times.indices) {

            val calendar = Calendar.getInstance()
            val timeParts = times[i].split(":")
            calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            calendar.set(Calendar.MINUTE, timeParts[1].toInt())
            calendar.set(Calendar.SECOND, 0)
            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            intent.putExtra("type", i)
            val pendingIntent = PendingIntent.getBroadcast(
                this@MainActivity,
                times[i].hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )


            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
            )
        }

    }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            } else {
                askForExactAlarmPermission()
            }
        } else {
            askForExactAlarmPermission()
        }
    }

    private fun askForExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    data = Uri.parse("package:$packageName")
                }
                startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM)
            } else {
                cancelAllAlarms(this@MainActivity)
                setAlarm()
            }
        } else {
            cancelAllAlarms(this@MainActivity)
            setAlarm()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    askForExactAlarmPermission()
                }
            }
        }
    }
    private fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                cancelAllAlarms(this@MainActivity)
                setAlarm()
            }
        }
    }


}