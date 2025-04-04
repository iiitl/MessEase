package com.theayushyadav11.MessEase.notifications

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.utils.Constants.Companion.USERS
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Mess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class PushNotifications(private val context: Context, private val target: String) {

    private val TAG = "FCM"
    private val client = OkHttpClient()
    private var accessToken: String = ""
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val fcmUrl = "https://fcm.googleapis.com/v1/projects/messease-b3b3f/messages:send"
    private val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    private val mess=Mess(context)

    init {
        coroutineScope.launch {
            updateAccessToken()
        }
        Log.d(TAG, "Initialized notification for target: $target")
    }

    fun sendNotificationToAllUsers(title: String, message: String) {
        coroutineScope.launch {
            try {
                getTokens { tokens, names ->
                    if (tokens.isNotEmpty()) {
                        coroutineScope.launch {
                            sendNotification(tokens, names, title, message)
                        }
                    } else {
                        mess.log( "No tokens found for target: $target")
                    }
                }
            } catch (e: Exception) {
                mess.log( "Error getting user tokens: ${e.message}")
            }
        }
    }

    private fun getTokens(onResult: (List<String>, List<String>) -> Unit) {
        firestoreReference.collection(USERS).get()
            .addOnSuccessListener { snapshot ->
                val tokens = mutableListOf<String>()
                val names = mutableListOf<String>()

                snapshot.documents.forEach { document ->
                    document.toObject(User::class.java)?.let { user ->
                        if (isUserTargeted(user) && user.token.length > 1) {
                            tokens.add(user.token)
                            names.add(user.email)
                        }
                    }
                }

                mess.log( "Found ${tokens.size} tokens for target: $target")
                onResult(tokens, names)
            }
            .addOnFailureListener { e ->
                mess.log("Failed to fetch users: ${e.message}")
                onResult(emptyList(), emptyList())
            }
    }

    private fun isUserTargeted(user: User): Boolean {
        return target.contains(user.batch) &&
                target.contains(user.passingYear) &&
                target.contains(user.gender)
    }

    private suspend fun updateAccessToken() {
        withContext(Dispatchers.IO) {
            try {
                context.resources.openRawResource(R.raw.messease).use { stream ->
                    val credentials = GoogleCredentials.fromStream(stream)
                        .createScoped("https://www.googleapis.com/auth/firebase.messaging")
                    credentials.refresh()
                    accessToken = credentials.accessToken.tokenValue
                    Log.d(TAG, "Access token updated successfully")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating access token: ${e.message}")
            }
        }
    }

    private suspend fun sendNotification(
        tokens: List<String>,
        names: List<String>,
        title: String,
        message: String
    ) {
        // Ensure token is valid before sending
        if (accessToken.isEmpty()) {
            updateAccessToken()
        }

        mess.log("Sending notification to ${tokens.size} users")

        // Create a mapping of tokens to names for easier lookup
        val tokenToName = tokens.zip(names).toMap()

        tokens.forEach { token ->
            val notificationJson = createNotificationJson(token, title, message)
            val requestBody = notificationJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .header("Authorization", "Bearer $accessToken")
                .url(fcmUrl)
                .post(requestBody)
                .build()

            try {
                withContext(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            mess.log( "Notification sent successfully to ${tokenToName[token]}")
                        } else {
                            Log.e(TAG, "Notification sending failed: ${response.body?.string()}")
                        }
                    }
                }
            } catch (e: IOException) {
                mess.log("Notification sending failed: ${e.message}")
            }
        }
    }

    private fun createNotificationJson(token: String, title: String, message: String): JSONObject {
        return JSONObject().apply {
            put("message", JSONObject().apply {
                put("token", token)
                put("notification", JSONObject().apply {
                    put("title", title)
                    put("body", message)
                })
                put("data", JSONObject().apply {
                    put("message", message)
                })
            })
        }
    }
}