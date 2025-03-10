package com.theayushyadav11.MessEase.utils

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.theayushyadav11.MessEase.Models.DayMenu
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.Models.Particulars
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.utils.Constants.Companion.TAG
import com.theayushyadav11.MessEase.utils.Constants.Companion.UPDATE
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.storageReference

class FireBase {
    fun getUser(uid: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        firestoreReference.collection("Users").whereEqualTo("uid", uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onFailure(error)
                    return@addSnapshotListener
                }
                val doc = value?.documents?.get(0)
                if (doc != null) {
                    onSuccess(doc.toObject(User::class.java)!!)
                }
            }
    }

    fun uploadFile(
        uri: Uri,
        path: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = storageReference.child(path)
        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                onSuccess(it.toString())
            }
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun uploadphoto(
        uri: ByteArray,
        path: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = storageReference.child(path)
        ref.putBytes(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                onSuccess(it.toString())
            }
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun deletefile(url: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)

        storageRef.delete().addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            if (exception.message.equals("Object does not exist at location.")) {
                onSuccess()
            } else
                onFailure(exception)
        }
    }

    fun getIcon(designation: String): Int {
        when (designation) {
            "Coordinator" -> return com.theayushyadav11.MessEase.R.drawable.coordinator
            "Member" -> return com.theayushyadav11.MessEase.R.drawable.member
            "Volunteer" -> return com.theayushyadav11.MessEase.R.drawable.volunteer
            "Senior-Member" -> return com.theayushyadav11.MessEase.R.drawable.seniormember
            "Developer" -> return com.theayushyadav11.MessEase.R.drawable.developer
            else ->
                return com.theayushyadav11.MessEase.R.drawable.logo
        }
    }

    fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                firestoreReference.collection("Users").document(auth.currentUser?.uid.toString())
                    .update("token", token)

            }
        }
    }

    fun deleteSubcollections(documentRef: DocumentReference, collection: String): Task<Void> {
        return documentRef.collection(collection).get()
            .continueWithTask { task ->
                val batch = firestoreReference.batch()
                val snapshot = task.result
                for (doc in snapshot.documents) {
                    Log.d(TAG, "Deleting ${doc.id}")
                    batch.delete(doc.reference)
                    deleteSubcollections(
                        doc.reference,
                        collection
                    ) // Recursively delete nested subcollections
                }
                batch.commit()
            }
    }

    fun getMainMenu(onResult: (Menu) -> Unit) {


        firestoreReference.collection("MainMenu").document("menu").addSnapshotListener { value, error ->
            if(error!=null)
            {
                onResult(Menu())
                return@addSnapshotListener
            }
            onResult(value?.toObject(Menu::class.java)!!)
        }
    }

    fun getUpdates(onResult: (String, String) -> Unit) {
        firestoreReference.collection("Update").document("update").get().addOnSuccessListener {
            val version = it.getString("version")
            val url = it.getString("url")
            if (version != null && url != null) {
                onResult(version, url)
            } else {

                onResult("", "")
            }

        }
            .addOnFailureListener {
                onResult("", "")
            }
    }

    fun getSenderDeatails(onResult: (String, String, String) -> Unit) {
        firestoreReference.collection("Sender").document("sender").get().addOnSuccessListener {
            val toEmail = it.getString("toEmail")
            val email = it.getString("email")
            val password = it.getString("password")
            if (toEmail != null && email != null && password != null) {
                onResult(email, password, toEmail)
            } else {
                onResult(
                    "theayushyadav11@gmail.com",
                    "hagd snwa yvpn vwwf",
                    "theayushyadav11b@gmail.com"
                )
            }

        }
            .addOnFailureListener {
                onResult(
                    "theayushyadav11@gmail.com",
                    "hagd snwa yvpn vwwf",
                    "theayushyadav11b@gmail.com"
                )
            }
    }
    fun runScripts(user: User) {
        addMenu(user)
    }

    private fun addMenu(user: User) {
        val menu = Menu(
            id = 0,
            comp = "",
            creator = user,
            menu = listOf(
                DayMenu(),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = "Spicy Matar Chhole, Kulcha, Milk, Tea, Banana/egg, Bread Butter/Jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),

                        Particulars(
                            type = "Dinner",
                            food = "Chole Bhature, Raita, Salad, Pickle, Sweet",
                            time = "8:00 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = "Spicy Matar Chhole, Kulcha, Milk, Tea, Banana/egg, Bread Butter/Jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = "Chole Bhature, Raita, Salad, Pickle, Sweet",
                            time = "8:00 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = "Spicy Matar Chhole, Kulcha, Milk, Tea, Banana/egg, Bread Butter/Jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = "Chole Bhature, Raita, Salad, Pickle, Sweet",
                            time = "8:00 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = "Spicy Matar Chhole, Kulcha, Milk, Tea, Banana/egg, Bread Butter/Jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = "Chole Bhature, Raita, Salad, Pickle, Sweet",
                            time = "8:00 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = "Spicy Matar Chhole, Kulcha, Milk, Tea, Banana/egg, Bread Butter/Jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = "Chole Bhature, Raita, Salad, Pickle, Sweet",
                            time = "8:00 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = "Spicy Matar Chhole, Kulcha, Milk, Tea, Banana/egg, Bread Butter/Jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = "Chole Bhature, Raita, Salad, Pickle, Sweet",
                            time = "8:00 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = "Spicy Matar Chhole, Kulcha, Milk, Tea, Banana/egg, Bread Butter/Jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "Rajma, Jeera Rice, Chapati, Salad, Pickle, Curd, Sweet",
                            time = "1:00 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = "Chole Bhature, Raita, Salad, Pickle, Sweet",
                            time = "8:00 PM to 9:30 PM"
                        )
                    )

                ),
            )

        )
        firestoreReference.collection("MainMenu").document("menu").set(menu)
        firestoreReference.collection(UPDATE).document("update").set(
            mapOf(
                "version" to "1.0",
                "url" to "https://github.com/theayushyadav11/MessEaseApp/releases/download/v1/MessEase.apk"
            )
        )
    }
}


