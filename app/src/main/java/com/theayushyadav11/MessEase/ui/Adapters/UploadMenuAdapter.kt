package com.theayushyadav11.MessEase.ui.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.Models.AprMenu
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDatabase
import com.theayushyadav11.MessEase.notifications.PushNotifications
import com.theayushyadav11.MessEase.ui.MessCommittee.activities.EditMenuActivity
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.MailSender
import com.theayushyadav11.MessEase.utils.Mess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UploadMenuAdapter(
    private val aprMenus: List<AprMenu>,
    val context: Context
) :
    RecyclerView.Adapter<UploadMenuAdapter.UploadMenuViewHolder>() {
    private val mess = Mess(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_element, parent, false)
        return UploadMenuViewHolder(view)
    }

    override fun getItemCount() = aprMenus.size

    override fun onBindViewHolder(holder: UploadMenuViewHolder, position: Int) {
        val aprMenu = aprMenus[position]
        holder.name.text =
            "${aprMenu.menu.creator.name}\n${aprMenu.menu.creator.batch}-${aprMenu.menu.creator.passingYear} "
        holder.other.text = aprMenu.note
        holder.time.text = aprMenu.displayDate
        holder.emal.text = aprMenu.menu.creator.email
        holder.upload.setOnClickListener {
            mess.showAlertDialog(
                "Confirm",
                "Are you sure you want to upload this menu?",
                "Upload Menu",
                "Cancel"
            ) {
                uploadMenu(aprMenu)
            }

        }
        holder.delete.setOnClickListener {
            mess.showAlertDialog(
                "Confirm",
                "Are you sure you want to delete this menu?",
                "Delete Menu",
                "Cancel"
            ) {
                deleteMenu(aprMenu, true)
            }
        }
        holder.main.setOnClickListener {
            mess.addPb("Loading..,")
            GlobalScope.launch(Dispatchers.IO) {
                val menu = Menu(id = 2, menu = aprMenu.menu.menu, creator = aprMenu.menu.creator)
                MenuDatabase.getDatabase(context).menuDao().addMenu(menu)
                mess.pbDismiss()
                startActivity(
                    context,
                    Intent(
                        context,
                        com.theayushyadav11.MessEase.ui.more.ShowMenuActivity::class.java
                    ),
                    null
                )
            }
        }
        holder.edit.setOnClickListener {
          edit(aprMenu)
        }
    }

    private fun edit(aprMenu: AprMenu) {
        mess.addPb("Loading..,")
        GlobalScope.launch(Dispatchers.IO) {
            val menu = Menu(id = 3, menu = aprMenu.menu.menu, creator = aprMenu.menu.creator)
            MenuDatabase.getDatabase(context).menuDao().addMenu(menu)
            mess.pbDismiss()
            startActivity(
                context,
                Intent(
                    context,
                    EditMenuActivity::class.java
                ),
                null
            )
        }
    }

    private fun deleteMenu(aprMenu: AprMenu, y: Boolean) {
        if (y) {
            mess.addPb("Deleting menu...")
        }
        firestoreReference.collection("MenuForApproval").document(aprMenu.key).delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (y) {
                        fireBase.deletefile(aprMenu.url,
                            onSuccess = {
                                mess.pbDismiss()
                                mess.toast("Menu deleted successfully")
                            },
                            onFailure = {
                                mess.pbDismiss()
                                mess.toast("Failed to delete menu")
                            })

                    }

                } else {
                    if (y) {
                        mess.pbDismiss()
                        mess.toast("Failed to delete menu")
                    }
                }
            }
    }

    private fun uploadMenu(aprMenu: AprMenu) {
        mess.addPb("Uploading menu...")
        firestoreReference.collection("MainMenu").document("menu").set(aprMenu.menu)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    deleteUrlandUploadNewUrl(aprMenu, onSuccess = {
                        mess.pbDismiss()
                        mess.toast("Menu uploaded successfully")
                        sendMail(aprMenu.url)
                     val pn= PushNotifications(context, "Batch - 2024Batch - 2025Batch - 2026Batch - 2027Batch - 2028Batch - 2029FemaleMale    Btech    Mtech   MBA     Mtech   ")
                     pn.sendNotificationToAllUsers("New Mess Menu has been updated", "Go and take a look at the new menu.")

                    }, onFailure = {
                        mess.pbDismiss()
                        mess.toast("Failed to upload menu")
                    })
                } else {
                    mess.pbDismiss()
                    mess.toast("Failed to upload menu")
                }
            }

    }

    fun sendMail(url: String) {

        fireBase.getSenderDeatails { email, password, toEmail ->
            val mailSender = MailSender(email,password)

            mailSender.sendEmailWithAttachment(
                toEmail,
                "Updated Mess Menu",
                "Greetings everyone,\n" +
                        "We are introducing the new mess menu of this month and hope y'all will enjoy it\n" +
                        "For your reference the new menu is attached below.\n" +
                        "Mess Committee\n" +
                        "IIIT Lucknow",
                url

            )
        }
    }

    private fun deleteUrlandUploadNewUrl(
        aprMenu: AprMenu,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        firestoreReference.collection("MainMenu").document("url").get()
            .addOnSuccessListener { value ->

                val url2 = value?.getString("url")
                if (url2 != null) {
                    fireBase.deletefile(url2,
                        onSuccess = {
                            firestoreReference.collection("MainMenu").document("url")
                                .set(hashMapOf("url" to aprMenu.url)).addOnCompleteListener {
                                    firestoreReference.collection("MenuForApproval")
                                        .document(aprMenu.key).delete()


                                    onSuccess()
                                }
                        },
                        onFailure = {
                            onFailure()
                        })
                }
            }.addOnFailureListener { error ->
                onFailure()
            }


    }

    inner class UploadMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val main: ConstraintLayout = itemView.findViewById(R.id.main)
        val name: TextView = itemView.findViewById(R.id.other)
        val other: TextView = itemView.findViewById(R.id.mname)
        val emal: TextView = itemView.findViewById(R.id.email)
        val upload: ImageView = itemView.findViewById(R.id.upload)
        val delete: ImageView = itemView.findViewById(R.id.delete)
        val time: TextView = itemView.findViewById(R.id.time)
        val edit:ImageView = itemView.findViewById(R.id.edit)
    }

}
