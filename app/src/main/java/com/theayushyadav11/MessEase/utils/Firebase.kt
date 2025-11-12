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
import com.theayushyadav11.MessEase.utils.Constants.Companion.COORDINATOR
import com.theayushyadav11.MessEase.utils.Constants.Companion.DEVELOPER
import com.theayushyadav11.MessEase.utils.Constants.Companion.MAIN_MENU
import com.theayushyadav11.MessEase.utils.Constants.Companion.MEMBER
import com.theayushyadav11.MessEase.utils.Constants.Companion.MENU
import com.theayushyadav11.MessEase.utils.Constants.Companion.SENIOR_MEMBER
import com.theayushyadav11.MessEase.utils.Constants.Companion.TAG
import com.theayushyadav11.MessEase.utils.Constants.Companion.UPDATE
import com.theayushyadav11.MessEase.utils.Constants.Companion.USERS
import com.theayushyadav11.MessEase.utils.Constants.Companion.UID
import com.theayushyadav11.MessEase.utils.Constants.Companion.VOLUNTEER
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.storageReference

class FireBase {
    fun getUser(uid: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        firestoreReference.collection(USERS).whereEqualTo(UID, uid)
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
            COORDINATOR -> return com.theayushyadav11.MessEase.R.drawable.coordinator
            MEMBER -> return com.theayushyadav11.MessEase.R.drawable.member
            VOLUNTEER -> return com.theayushyadav11.MessEase.R.drawable.volunteer
            SENIOR_MEMBER -> return com.theayushyadav11.MessEase.R.drawable.seniormember
            DEVELOPER -> return com.theayushyadav11.MessEase.R.drawable.developer
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
                            food = " Spicy Matar Chhole,\n" +
                                    " Kulcha, Milk, Tea,\n" +
                                    " Banana/egg,Bread,\n" +
                                    " butter/jam ",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "  Vegetable Biryani ,Dal\n" +
                                    " Makhni, Tawa Paratha,\n" +
                                    " Raita",
                            time = "12:30 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = " Bhel, tea",
                            time = "5:00 PM to 6:00 PM"
                        ),

                        Particulars(
                            type = "Dinner",
                            food = "Sewai ,Jeera\n" +
                                    " Rice, Roti,Malai\n" +
                                    " Kofta ,Punjabi\n" +
                                    " Dal Tadka ",
                            time = "7:30 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = " Pav Bhaji ,Daliya,Milk,\n" +
                                    " Tea ,Banana /Egg -1pc,\n" +
                                    " Bread Butter/jam ",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "  Spicy Chhola,\n" +
                                    " Poori,\n" +
                                    " Curd,\n" +
                                    " Jeera Rice",
                            time = "12:30 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "  Maggi, Tea",
                            time = "5:00 PM to 6:00 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = "Arahar Dal Fry,\n" +
                                    " Baigan Bharta,\n" +
                                    " Roti,\n" +
                                    " Rice ",
                            time = "7:30 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = " Medu vada,\n" +
                                    " Coconut chutney,\n" +
                                    " sambhar,\n" +
                                    " milk,\n" +
                                    " tea,\n" +
                                    " Banana/egg,\n" +
                                    " Bread, butter/jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = " Rajma Masala,\n" +
                                    " Boondi Raita,\n" +
                                    " Roti,\n" +
                                    " Jeera Rice ",
                            time = "12:30 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "Bread Pakoda,\n" +
                                    " Sauce,Chutney ,\n" +
                                    " Tea ",
                            time = "5:00 PM to 6:00 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = " Lauki Kofta,\n" +
                                    " vegetable tahri roti raita\n" +
                                    " thick ",
                            time = "7:30 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = "Aloo paratha,\n" +
                                    " curd, milk, tea,\n" +
                                    " Banana/egg,\n" +
                                    " Bread, Butter/jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "Sambhar,\n" +
                                    " Roti,\n" +
                                    " Rice ,\n" +
                                    " Aaloo Gobi",
                            time = "12:30 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = "Pyaz Bhajiya, chutney,Tea",
                            time = "5:00 PM to 6:00 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = " Shahi Paneer, Zeera rice,\n" +
                                    " Mix Dal fry, Jalebi/\n" +
                                    " Moong daal halwa ",
                            time = "7:30 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = " Aloo Puri, Banana/\n" +
                                    " egg,\n" +
                                    " Milk,Tea,\n" +
                                    " Bread,Butter/Jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "  Aaloo Pyaz Bhujia,Kadhi\n" +
                                    " pyaaz\n" +
                                    " pakoda,\n" +
                                    " rice ,roti",
                            time = "12:30 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = " Chola Samosa,Tea",
                            time = "5:00 PM to 6:00 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = " Aloo matar gajar,\n" +
                                    " Rice,Roti,\n" +
                                    " Dal palak",
                            time = "7:30 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = " Gobi Paratha,\n" +
                                    " Banana/egg 1 pc\n" +
                                    " bread\n" +
                                    " Butter jam\n" +
                                    " Milk,Tea, ",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = "  Aloo Palak,\n" +
                                    " Arahar dal Fry,\n" +
                                    " Roti,Rice",
                            time = "12:30 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = " Aloo tikki matar chaat\n" +
                                    " with dahi and chutney",
                            time = "5:00 PM to 6:00 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = " Bhandara style sabji,\n" +
                                    " Poori , Black Masoor Dal,\n" +
                                    " Rice",
                            time = "7:30 PM to 9:30 PM"
                        )
                    )

                ),
                DayMenu(
                    listOf(
                        Particulars(
                            type = "Breakfast",
                            food = " Utappam, sambhar,\n" +
                                    " coconut chutney,\n" +
                                    " Milk, tea,\n" +
                                    " banana/egg, Bread,\n" +
                                    " butter/jam",
                            time = "8:30 AM to 10:00 AM"
                        ),
                        Particulars(
                            type = "Lunch",
                            food = " Pindi Choley\n" +
                                    " Bathure\n" +
                                    " Rice\n" +
                                    " Boondi Raita",
                            time = "12:30 PM to 2:30 PM"
                        ),
                        Particulars(
                            type = "Snacks",
                            food = " Rusk(5 pcs) ,Tea",
                            time = "5:00 PM to 6:00 PM"
                        ),
                        Particulars(
                            type = "Dinner",
                            food = "Veg\n" +
                                    " Jalfrezi,Arhar\n" +
                                    " Dal Fry\n" +
                                    " Roti, JeeraRice",
                            time = "7:30 PM to 9:30 PM"
                        )
                    )

                ),
            )

        )
        firestoreReference.collection(MAIN_MENU).document(MENU).set(menu)
        firestoreReference.collection(UPDATE).document("update").set(
            mapOf(
                "version" to "1.2",
                "url" to "https://github.com/iiitl/MessEase/releases/tag/release"
            )
        )
    }
}
