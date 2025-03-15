package com.theayushyadav11.MessEase.utils

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDatabase
import com.theayushyadav11.MessEase.databinding.EditDialogBinding
import com.theayushyadav11.MessEase.databinding.SelTargetDialogBinding
import com.theayushyadav11.MessEase.utils.Constants.Companion.TAG
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class Mess(context: Context) {
    var context: Context
    private var loadingDialog: Dialog? = Dialog(context)
    private var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val designation = MutableLiveData<String>()

    init {
        this.context = context
    }

    fun save(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun get(key: String): String {
        return sharedPreferences.getString(key, "").toString()
    }

    fun get(key: String, defVal: String): String {
        return sharedPreferences.getString(key, defVal).toString()
    }

    fun sendPollId(id: String) {
        save("pollId", id)
    }

    fun getPollId(): String {
        return get("pollId")
    }

    fun setIsLoggedIn(isMember: Boolean) {
        save("isLoggedIn", isMember.toString())
    }

    fun isLoggedIn(): Boolean {
        if (get("isLoggedIn") == "true") {
            return true
        } else {
            return false
        }
    }

    fun addPb(message: String) {
        loadingDialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            val layout = LayoutInflater.from(context).inflate(R.layout.loading, null)
            val tvMsg = layout.findViewById<TextView>(R.id.msg)
            tvMsg.text = message
            setContentView(layout)
            setCancelable(false)

//            window?.setBackgroundDrawableResource(android.R.color.transparent)
//            window?.setLayout(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
            show()
        }
    }

    fun pbDismiss() {
        loadingDialog?.setCancelable(true)
        loadingDialog?.dismiss()
    }

    fun toast(message: Any) {
        Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show()
    }

    fun snack(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    fun log(message: Any) {
        Log.d(TAG, message.toString())
    }

    fun showInputDialog(
        hint: String,
        onOkClicked: (String) -> Unit,
        onCancelClicked: (String) -> Unit
    ) {
        val dialog = Dialog(context)
        val bind = EditDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bind.root)
        dialog.setCancelable(false)
        dialog.show()
        bind.textInputLayout3.hint = hint
        bind.cancel.setOnClickListener {
            onCancelClicked("")
            dialog.dismiss()
        }
        bind.done.setOnClickListener {
            val d = bind.etUpdate.text.toString()
            if (d.isEmpty()) {
                toast("This feild cannot be empty!")
            } else {
                onOkClicked(d)
                dialog.dismiss()
            }
        }
    }

    fun showAlertDialog(
        title: String,
        message: String,
        okText: String,
        cancelText: String,
        onResult: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setCancelable(false)
        builder.setMessage(message)
        builder.setPositiveButton(okText) { dialog, _ ->
            onResult()
            dialog.dismiss()
        }
        builder.setNegativeButton(cancelText) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    fun openDialog(type: String, onResult: (String) -> Unit) {
        val dialog = Dialog(context)
        val bind = SelTargetDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bind.root)
        dialog.setCancelable(false)
        bind.title.text = "Select the recipients for $type"
        bind.btnAddPoll.text = "Send $type"
        val c = (Date().year + 1900)
        val batches = listOf(c, c + 1, c + 2, c + 3, c + 4, c + 5)
        val rbList: List<RadioButton> = listOf(
            bind.rb0,
            bind.rb1,
            bind.rb2,
            bind.rb3,
            bind.rb4,
            bind.rb5,
            bind.rbGirl,
            bind.rbBoy,
            bind.rbBtech,
            bind.rbMtech,
            bind.rbMba,
            bind.rbMsc
        )

        for (i in 0 until batches.size) {
            rbList[i].setText("Batch - ${batches[i]}")
        }
        bind.cb.setOnCheckedChangeListener { buttonView, isChecked ->
            for (i in rbList) {
                i.isChecked = isChecked
            }
        }
        bind.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        bind.btnAddPoll.setOnClickListener {
            var target = ""
            for (i in rbList) {
                if (i.isChecked) {
                    target += i.text
                }
            }
            var yearSelected = false
            var genderSelected = false
            var batchSelected = false
            for (i in 0 until rbList.size) {
                if (i < 6 && rbList[i].isChecked) yearSelected = true
                if (i > 5 && i < 8 && rbList[i].isChecked) genderSelected = true
                if (i > 7 && rbList[i].isChecked) batchSelected = true
            }
            if (!yearSelected) {
                toast("Please Select a year")
            } else if (!genderSelected) {
                toast("Please Select gender")
            } else if (!batchSelected) {
                toast("Please Select a batch")
            } else {
                onResult(target)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun loadImage(url: String, view: ImageView) {
        Glide.with(context).load(url).into(view)
    }

    fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun loadCircularImage(url: String, view: ImageView) {
        Glide.with(context).load(url).circleCrop().error(R.drawable.profile_circle).into(view)
    }

    fun showImage(photo: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.img2)
        val img = dialog.findViewById<ImageView>(R.id.img)
        loadImage(photo, img)
        img.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun getmanager(): RecyclerView.LayoutManager {
        val layoutManager = object : LinearLayoutManager(context) {
            override fun onLayoutChildren(
                recycler: RecyclerView.Recycler?,
                state: RecyclerView.State?
            ) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                }
            }

            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        return layoutManager
    }

    fun getLists(listName: String, onResult: (List<String>) -> Unit) {
        firestoreReference.collection("Lists").document("lists").get().addOnSuccessListener {
            val list = it.get(listName) as? List<String> ?: emptyList()
            onResult(list)
        }
    }

    private fun cont(email: String, onResult: (Boolean) -> Unit) {
        getLists("allows") {
            if (it.contains(email)) {
                onResult(true)
            }
            onResult(false)
        }
    }

    fun isValidEmail(email: String, onResult: (Boolean) -> Unit) {
        try {
            val emailR = email.substring(0, email.indexOf("@"))
            var isValid = true
            if (emailR.contains("+") || emailR.contains(".") || emailR.contains("-") ||
                emailR.contains(
                    "_"
                ) || emailR.contains(
                    "/"
                ) || emailR.contains("*") || emailR.contains("#") || emailR.contains("!") ||
                emailR.contains(
                    "$"
                ) || emailR.contains("%") || emailR.contains("^") || emailR.contains(
                    "&"
                ) || emailR.contains("(") || emailR.contains(")") || emailR.contains("=") ||
                emailR.contains(
                    "{"
                ) || emailR.contains("}") || emailR.contains("[") || emailR.contains("]") ||
                emailR.contains(
                    ":"
                ) || emailR.contains(";") || emailR.contains(",") || emailR.contains("<") ||
                emailR.contains(
                    ">"
                ) || emailR.contains("?") || emailR.contains("|") || emailR.contains("`") ||
                emailR.contains(
                    "~"
                )
            ) {
                isValid = false
            }
            cont(email) {
                if (it || (
                            (email.endsWith("@iiitl.ac.in") && isValid) || email.contains(
                                "ayushyadav"
                            )
                            )
                ) {
                    onResult(
                        true
                    )
                } else {
                    onResult(false)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun downloadFile(url: String) {
        val title = "MessMenu.pdf"
        val description = "Downloading file"
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(title)
        request.setDescription(description)
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        )
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)
        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
    }

    fun setUser(user: User) {
        val s =
            user.uid + "#" + user.name + "#" + user.token + "#" + user.member + "#" +
                    user.photoUrl +
                    "#" + user.email + "#" + user.designation + "#" + user.batch + "#" +
                    user.passingYear + "#" + user.gender + "#"
        save("user", s)
    }

    fun getUser(): User {
        try {
            val s = get("user")
            val a = s.split("#")
            Log.d(TAG, a.toString())
            return User(a[0], a[1], a[2], a[3].toBoolean(), a[4], a[5], a[6], a[7], a[8], a[9])
        } catch (e: Exception) {
            return User()
        }
    }

    fun getUpdates(onResult: (String, String) -> Unit) {
        val s = get("update")
        val a = s.split("#")
        onResult(a[0], a[1])
    }

    fun setUpdate(version: String, url: String) {
        val s = "$version#$url"
        save("update", s)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getMainMenu(onResult: (Menu) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val menu = MenuDatabase.getDatabase(context).menuDao().getMenu()
            withContext(Dispatchers.Main) {
                onResult(menu)
            }
        }
    }
}