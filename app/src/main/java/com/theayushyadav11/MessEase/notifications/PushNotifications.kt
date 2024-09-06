package com.theayushyadav11.MessEase.notifications

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials

import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class PushNotifications(private val context: Context, private val target: String) {

    private val client = OkHttpClient()
    private var accessToken: String = ""
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    init {
        coroutineScope.launch {
            updateAccessToken()
        }
        Log.d("FCM", "Sending notification to${target}  users")
    }


    fun sendNotificationToAllUsers(title:String,message:String) {
        coroutineScope.launch {
            try {
                getTokens { tokens,names ->
                    if (tokens.isNotEmpty()) {
                        coroutineScope.launch {
                            sendNotification(tokens,names, title, message)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("FCM", "Error getting user tokens: ${e.message}")
            }
        }
    }

    private fun getTokens(onResult: (List<String>,List<String>) -> Unit) {
        firestoreReference.collection("Users").addSnapshotListener { value, error ->
            if (error != null) {
                onResult(emptyList(), emptyList())
                return@addSnapshotListener
            }
            val tokens = mutableListOf<String>()
            val names = mutableListOf<String>()
            for (document in value?.documents!!) {
                val user = document.toObject(User::class.java)!!
                if (target.contains(user.batch) && target.contains(user.passingYear) && target.contains(user.gender)) {
                    if (user.token.length>1)
                    {
                        user.token.let { tokens.add(it) }
                        names.add(user.email)
                    }

                }
            }
            onResult(tokens,names)
        }
    }

    private suspend fun updateAccessToken() {
        withContext(Dispatchers.IO) {
            try {
                val stream = context.resources.openRawResource(R.raw.messease)
                val credentials = GoogleCredentials.fromStream(stream)
                    .createScoped("https://www.googleapis.com/auth/firebase.messaging")
                credentials.refresh()
                accessToken = credentials.accessToken.tokenValue
            } catch (e: Exception) {
                Log.e("FCM", "Error updating access token: ${e.message}")
            }
        }
    }

    private suspend fun sendNotification(tokens: List<String>,names:List<String>, title: String,message: String) {
        updateAccessToken()
        Log.d("FCM", "Sending notification to ${names} users")
        tokens.forEach { token ->
            val json = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("token", token)
                    put("notification", JSONObject().apply {
                        put("title", title)
                        put("body",  message)
                    })
                    put("data", JSONObject().apply {
                        put("message", message)
                    })
                })
            }

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val request = Request.Builder()
                .header("Authorization", "Bearer $accessToken")
                .url("https://fcm.googleapis.com/v1/projects/messease-b3b3f/messages:send")
                .post(body)
                .build()

            try {
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }
                if (response.isSuccessful) {
                    Log.d("FCM", "Notification sent successfully to ${names[tokens.indexOf(token)]}")
                } else {
                    Log.e("FCM", "Notification sending failed: ${response.body?.string()}")
                }
            } catch (e: IOException) {
                Log.e("FCM", "Notification sending failed: ${e.message}")
            }
        }
    }
}
