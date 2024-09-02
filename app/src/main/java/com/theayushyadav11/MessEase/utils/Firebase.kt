package com.theayushyadav11.MessEase.utils

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.utils.Constants.Companion.TAG
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.storageReference

class FireBase {


    fun getUser(uid: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        firestoreReference.collection("Users").whereEqualTo("uid", uid)
            .addSnapshotListener { it, error ->
                if (it?.documents!!.isNotEmpty()) {
                    val doc = it.documents?.get(0)
                    if (doc != null) {
                        onSuccess(doc.toObject(User::class.java)!!)
                    } else {
                        error?.let { it1 -> onFailure(it1) }
                    }
                }

            }
    }

    fun uploadfile(
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
        firestoreReference.collection("MainMenu").document("menu").get().addOnSuccessListener {
            onResult(it.toObject(Menu::class.java)!!)
        }
            .addOnFailureListener {
                onResult(Menu())
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
}

