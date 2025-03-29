package com.theayushyadav11.MessEase

import android.Manifest
import android.app.AlarmManager
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.databinding.ActivityMainBinding
import com.theayushyadav11.MessEase.notifications.AlarmReceiver
import com.theayushyadav11.MessEase.ui.MessCommittee.activities.MessCommitteeMain
import com.theayushyadav11.MessEase.ui.more.PaymentActivity
import com.theayushyadav11.MessEase.ui.more.ReviewActivity
import com.theayushyadav11.MessEase.ui.more.SettingsActivity
import com.theayushyadav11.MessEase.ui.splash.fragments.LoginAndSignUpActivity
import com.theayushyadav11.MessEase.utils.Constants.Companion.COORDINATOR
import com.theayushyadav11.MessEase.utils.Constants.Companion.DEVELOPER
import com.theayushyadav11.MessEase.utils.Constants.Companion.MAIN_MENU
import com.theayushyadav11.MessEase.utils.Constants.Companion.MENU_ALERTS_CHANNEL_DESCRIPTION
import com.theayushyadav11.MessEase.utils.Constants.Companion.MENU_ALERTS_CHANNEL_NAME
import com.theayushyadav11.MessEase.utils.Constants.Companion.TAG
import com.theayushyadav11.MessEase.utils.Constants.Companion.TOKEN
import com.theayushyadav11.MessEase.utils.Constants.Companion.URL
import com.theayushyadav11.MessEase.utils.Constants.Companion.USERS
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Mess
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mess: Mess
    private lateinit var alarmManager: AlarmManager
    private lateinit var analytics: FirebaseAnalytics

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, "Download completed!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1
        private const val REQUEST_CODE_SCHEDULE_EXACT_ALARM = 2
        private const val NOTIFICATION_CHANNEL_ID = "DailyNotification"

        fun cancelAllAlarms(context: Context,index:Int) {
            try {
                val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmReceiver::class.java)

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    index,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )

                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                    Log.d(TAG, "All alarms cancelled")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mess = Mess(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySavedTheme()
        initialise()
        setUpNav()
        setUpHeader()
        createNotificationChannel()
        askForNotificationPermission()
        fireBase.getToken()
        setAlarm()
        listeners()
    }
    private fun applySavedTheme() {
        val isNightMode = mess.get("isNightMode","false").toBoolean()
        val newMode = if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(newMode)
    }

    private fun toggleTheme() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        val newMode = if (isNightMode) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }

        AppCompatDelegate.setDefaultNightMode(newMode)

        mess.save("isNightMode", (!isNightMode).toString())
    }
    private fun listeners() {
//        darkModeSwitch.setOnCheckedChangeListener{
//           _, isChecked->
//            toggleTheme()
//        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initialise() {
        mess = Mess(this)
        mess.setIsLoggedIn(true)
        analytics = FirebaseAnalytics.getInstance(this)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            RECEIVER_NOT_EXPORTED
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
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
                startActivity(Intent(this, ReviewActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setDrawerClickListener(
        navView: NavigationView,
        drawerLayout: DrawerLayout,
        navController: NavController
    ) {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    handleLogout()
                    true
                }
                R.id.nav_messCommitteeActivity -> {
                    handleMessCommitteeNavigation()
                    true
                }
                R.id.nav_download -> {
                    downloadMenu()
                    true
                }
                R.id.nav_darkmode -> {
                    toggleTheme()
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    private fun handleLogout() {
        try {
            val userId = auth.currentUser?.uid
            if (!userId.isNullOrEmpty()) {
                firestoreReference.collection(USERS)
                    .document(userId)
                    .update(TOKEN, "")
                    .addOnFailureListener { e ->
                        mess.log("Failed to update token: ${e.message}")
                    }
            }

            signOut()
            mess.setIsLoggedIn(false)
            mess.setUser(User())

            val intent = Intent(this@MainActivity, LoginAndSignUpActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            mess.log("Logout error: ${e.message}")
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleMessCommitteeNavigation() {
        val user = mess.getUser()
        if (user.member && auth.currentUser != null) {
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
    }

    private fun downloadMenu() {
        firestoreReference.collection(MAIN_MENU).document(URL).get()
            .addOnSuccessListener { value ->
                val url = value?.get(URL).toString()
                if (url.isNotEmpty()) {
                    mess.downloadFile(url)
                } else {
                    mess.toast("Menu URL not found")
                }
            }.addOnFailureListener { error ->
                mess.toast("Failed to download menu: ${error.message}")
                mess.log("Download menu error: ${error.message}")
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
        val user = mess.getUser()
        admin.isVisible = (user.designation == COORDINATOR || user.designation == DEVELOPER)

    }

    private fun setUpHeader() {
        if (isFinishing || isDestroyed) return

        try {
            val headerView: View = binding.navView.getHeaderView(0)
            val layout: LinearLayout = headerView.findViewById(R.id.navMain)
            val user = mess.getUser()

            mess.loadCircularImage(user.photoUrl, layout.findViewById(R.id.propic))
            layout.findViewById<TextView>(R.id.mname).text = user.name
            layout.findViewById<TextView>(R.id.email).text = user.email
        } catch (e: Exception) {
            mess.log("Error setting up header: ${e.message}")
        }
    }

    private fun signOut() {
        try {
            val mAuth = FirebaseAuth.getInstance()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            mAuth.signOut()
            mGoogleSignInClient.signOut().addOnCompleteListener(this) {
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            mess.log("Sign out error: ${e.message}")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                MENU_ALERTS_CHANNEL_NAME,
                importance
            ).apply {
                description = MENU_ALERTS_CHANNEL_DESCRIPTION
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshAlarms()
    }

    override fun onRestart() {
        super.onRestart()
        refreshAlarms()
    }

    private fun refreshAlarms() {
        try {
            setAlarm()
        } catch (e: Exception) {
            mess.log("Error refreshing alarms: ${e.message}")
        }
    }

    private fun setAlarm() {
        try {
            val intent = Intent(this@MainActivity, AlarmReceiver::class.java)

            val times = listOf(
                mess.get("bt", "7:30"),
                mess.get("lt", "12:0"),
                mess.get("st", "16:30"),
                mess.get("dt", "19:0")
            )

            mess.log("Setting alarms: Breakfast ${times[0]}, Lunch ${times[1]}, " +
                    "Snack ${times[2]}, Dinner ${times[3]}")

            for (i in times.indices) {
                scheduleAlarm(i, times[i], intent)
            }
        } catch (e: Exception) {
            mess.log("Error setting alarms: ${e.message}")
        }
    }

    private fun scheduleAlarm(index: Int, timeString: String, intent: Intent) {
        try {
            val calendar = Calendar.getInstance()
            val timeParts = timeString.split(":")

            if (timeParts.size < 2) {
                mess.log("Invalid time format: $timeString")
                return
            }

            val hour = timeParts[0].toIntOrNull() ?: return
            val minute = timeParts[1].toIntOrNull() ?: return

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            val uniqueIntent = Intent(intent).apply {
                putExtra("type", index)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                this@MainActivity,
                index,
                uniqueIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                    mess.log("Set exact repeating alarm for ${timeString}")
                } else {
                    alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                    mess.log("Set inexact repeating alarm for ${timeString}")
                }
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
                mess.log("Set repeating alarm for ${timeString}")
            }
        } catch (e: Exception) {
            mess.log("Error scheduling alarm for $timeString: ${e.message}")
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
            if (!alarmManager.canScheduleExactAlarms()) {
                try {
                    val intent = Intent().apply {
                        action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        data = Uri.parse("package:$packageName")
                    }
                    startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM)
                } catch (e: Exception) {
                    mess.log("Error requesting exact alarm permission: ${e.message}")
                    setAlarm()
                }
            } else {
                setAlarm()
            }
        } else {
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
                } else {
                    askForExactAlarmPermission()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            if (alarmManager.canScheduleExactAlarms()) {
                setAlarm()
            } else {
                setAlarm()
            }
        }
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(onDownloadComplete)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

}