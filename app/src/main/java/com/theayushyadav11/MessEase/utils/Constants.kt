package com.theayushyadav11.MessEase.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Constants {

    companion object {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid.toString()
        val databaseReference = FirebaseDatabase.getInstance().reference
        val firestoreReference: FirebaseFirestore by lazy { Firebase.firestore }
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val fireBase = FireBase()
        const val authority = "com.theayushyadav11.MessEase.provider"
        const val menuFileName = "Mess Menu.pdf"
        const val menuFileType = "application/pdf"
        const val TAG = "yatinManeesh"
        val ASCENDING_ORDER = com.google.firebase.firestore.Query.Direction.ASCENDING
        val DESCENDING_ORDER = com.google.firebase.firestore.Query.Direction.DESCENDING

        const val RAZORPAY_API_KEY = "test_U7Sr1TuI0xqh3w"
        const val LOGO_LINK =
            "https://github.com/user-attachments/assets/02c34e6a-2e85-4745-82b8-715d2fdda3df"
        const val PAYMENTS = "Payments"
        const val TIMESTAMP = "timestamp"

        // ****************************************************Some String Constants********************************************************
        const val COMPARER = "comp"
        const val MENU_ALERTS_CHANNEL_NAME = "menuAlerts"
        const val MENU_ALERTS_CHANNEL_DESCRIPTION =
            """Stay updated with the daily food menu.
               Get timely notifications about what's being served for breakfast,
                lunch, snacks, and dinner."""

        // Tables
        const val USERS = "Users"
        const val MESSAGES = "Msgs"
        const val POLLS = "Polls"
        const val POLL_RESULT = "PollResult"
        const val REVIEWS = "Reviews"
        const val MAIN_MENU = "MainMenu"
        const val MENU = "menu"
        const val LIST = "Lists"
        const val UPDATE = "Update"
        const val VERSION = "version"
        const val MENU_FOR_APPROVAL = "MenuForApproval"
        const val COMMENTS = "Comments"
        const val URL = "url"

        // User Fields
        const val BATCH = "batch"
        const val DESIGNATION = "designation"
        const val EMAIL = "email"
        const val NAME = "name"
        const val UID = "uid"
        const val GENDER = "gender"
        const val IS_MEMBER = "member"
        const val PASSING_YEAR = "passingYear"
        const val PHOTO_URL = "photoUrl"
        const val TOKEN = "token"

        // Designations

        const val DEVELOPER = "Developer"
        const val COORDINATOR = "Coordinator"
        const val MEMBER = "Member"
        const val SENIOR_MEMBER = "Senior-Member"
        const val VOLUNTEER = "Volunteer"

        // Polls Fields
        const val DATE = "date"
        const val SELECTED_OPTION = "selected"
        const val SPECIAL_MEAL = "SpecialMeal"

        const val ALL_USERS="Batch - 2024Batch - 2025Batch - 2026Batch - 2027Batch - 2028Batch - 2029FemaleMale    Btech    Mtech   MBA     Mtech   "
        const val FCM_URL = "https://fcm.googleapis.com/v1/projects/messease-b3b3f/messages:send"
        val MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()

        // ****************************************************Functions********************************************************

        fun getCurrentTimeInAmPm(): String {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }

        fun getKey(): String {
            return databaseReference.push().key.toString()
        }

        fun getCurrentDate(): String {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("${Date().date}/MM/yyyy", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }

        fun compressImage(context: Context, imageUri: Uri): ByteArray {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val imageData = baos.toByteArray()
            return imageData
        }

        fun formatTimeMillis(timeMillis: Long): String {
            val date = Date(timeMillis)
            val dateFormat =
                SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) // Apr 3, 4:48 PM
            return dateFormat.format(date)
        }


    }
}
