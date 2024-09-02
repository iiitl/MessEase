package com.theayushyadav11.MessEase.ui.MessCommittee.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDatabase
import com.theayushyadav11.MessEase.databinding.ActivityEditCompleteBinding
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModelFactories.EditCompleteViewModelFactory
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.EditCompleteViewModel
import com.theayushyadav11.MessEase.utils.Constants.Companion.authority
import com.theayushyadav11.MessEase.utils.Constants.Companion.menuFileName
import com.theayushyadav11.MessEase.utils.Constants.Companion.menuFileType
import com.theayushyadav11.MessEase.utils.Mess
import java.io.File

class EditCompleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditCompleteBinding
    private lateinit var mess: Mess
    private lateinit var editedMenu: Menu
    private lateinit var uri: Uri
    private val viewModel: EditCompleteViewModel by viewModels {
        val database = MenuDatabase.getDatabase(this)
        EditCompleteViewModelFactory(database.menuDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        listener()
    }

    fun initialise() {
        mess = Mess(this)
        setUpToolBar()
        initFilePaths()
        viewModel.getEditedMenu {
            editedMenu = it
        }
    }

    fun listener() {
        binding.button.setOnClickListener {
            openPDF()
        }
        binding.send.setOnClickListener {
            sendToApprove()
        }
    }

    fun initFilePaths() {
        val fileName = menuFileName
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        if (file.exists()) {
            uri = FileProvider.getUriForFile(
                this,
                authority,
                file
            )
        }
    }

    fun openPDF() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, menuFileType)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.message?.let { mess.toast(it) }
        }

    }

    private fun sendToApprove() {
        viewModel.ifSameExists(viewModel.getComp(editedMenu),
            onResult = {
                if (it) {
                    mess.showAlertDialog(
                        "Alert!",
                        "A menu with the same composition already exists.",
                        "Ok",
                        ""
                    ) {


                    }


                    return@ifSameExists
                } else {
                    mess.addPb("Sending...")
                    mess.showInputDialog("Add Note...",
                        onOkClicked = { note ->
                            viewModel.sendToApprove(uri, note, editedMenu,
                                onSuccess = {
                                    mess.toast("Sent for Approval")
                                    mess.pbDismiss()
                                    startActivity(Intent(this, MessCommitteeMain::class.java))
                                    finish()
                                },
                                onFailure = {
                                    mess.toast(it.message!!)
                                    mess.pbDismiss()
                                }
                            )
                        },
                        oncancelClicked = {
                            mess.pbDismiss()
                        }
                    )
                }
            },
            onError = {
                mess.toast(it)
            }
        )


    }

    fun setUpToolBar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Ensure that the support action bar is not null before setting the title
        supportActionBar?.apply {
            title = "Edit Complete"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}
