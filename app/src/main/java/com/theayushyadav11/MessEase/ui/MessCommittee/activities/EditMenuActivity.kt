package com.theayushyadav11.MessEase.ui.MessCommittee.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.theayushyadav11.MessEase.Models.DayMenu
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.Models.Particulars
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDatabase
import com.theayushyadav11.MessEase.databinding.ActivityMenuBinding
import com.theayushyadav11.MessEase.databinding.EditDialogBinding
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModelFactories.EditMenuViewModelFactory
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.EditMenuViewModel
import com.theayushyadav11.MessEase.utils.Mess
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EditMenuActivity : AppCompatActivity() {
    val REQUEST_CODE = 1232
    private lateinit var binding: ActivityMenuBinding
    private var texts: MutableList<MutableList<TextView>> = mutableListOf()
    private lateinit var mess: Mess
    private var isClicked = false
    private lateinit var currentMenu: Menu
    private var isEdited = false
    private val viewModel: EditMenuViewModel by viewModels {
        val database = MenuDatabase.getDatabase(this)
        EditMenuViewModelFactory(database.menuDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mess = Mess(this)
        askPermissions()
        initialise()
        assign()
        listeners()
    }

    private fun initialise() {
        mess = Mess(this)
        mess.addPb("Loading menu...")
        viewModel.getMenuCurrentMenu { menu ->
            currentMenu = menu
            viewModel.setCurrentMenu(menu)
            assign()
            setFood(menu.menu)
            mess.pbDismiss()
        }
    }

    private fun askPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        } else {
            setupNextButtonListener()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {


            setupNextButtonListener()
        }

        isClicked = false
    }

    private fun setFood(menu: List<DayMenu>) {
        for (i in 0..3) {
            for (j in 0..6) {
                texts[i][j].text =
                    menu[j + 1].particulars[i].food
            }
        }
    }

    private fun setupNextButtonListener() {

        binding.next.setOnClickListener {
            binding.next.isVisible = false
            pdfConversion()
            binding.next.isVisible = true
            getEditedMenu { menu ->
                mess.log("Menu: $menu")
                viewModel.setEditedMenu(menu)
            }
            startActivity(Intent(this@EditMenuActivity, EditCompleteActivity::class.java))
        }
    }

    private fun pdfConversion() {
        val horizontalScrollView = binding.root
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        horizontalScrollView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        horizontalScrollView.layout(
            0,
            0,
            horizontalScrollView.measuredWidth,
            horizontalScrollView.measuredHeight
        )
        val document = PdfDocument()
        val viewWidth = horizontalScrollView.measuredWidth
        val viewHeight = horizontalScrollView.measuredHeight
        val pageInfo = PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        horizontalScrollView.draw(canvas)
        document.finishPage(page)

        val fileName = "Mess Menu.pdf"
        val filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)


        try {
            if (filePath.exists()) {
                filePath.delete()
            }
            if (filePath.parentFile?.exists() == false) {
                filePath.parentFile?.mkdirs()
            }
            val fos = FileOutputStream(filePath)
            document.writeTo(fos)
            document.close()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun assign() {
        var l = listOf(
            binding.sube,
            binding.mobe,
            binding.tube,
            binding.webe,
            binding.thbe,
            binding.frbe,
            binding.sabe,

            )
        texts.add(l.toMutableList())

        l = listOf(
            binding.sulu,
            binding.molu,
            binding.tulu,
            binding.welu,
            binding.thlu,
            binding.frlu,
            binding.salu,

            )
        texts.add(l.toMutableList())

        l = listOf(
            binding.susn,
            binding.mosn,
            binding.tusn,
            binding.wesn,
            binding.thsn,
            binding.frsn,
            binding.sasn,

            )
        texts.add(l.toMutableList())

        l = listOf(
            binding.sudi,
            binding.modi,
            binding.tudi,
            binding.wedi,
            binding.thdi,
            binding.frdi,
            binding.sadi,

            )
        texts.add(l.toMutableList())
    }

    private fun listeners() {
        for (i in 0 until texts.size) {
            for (j in 0 until texts[i].size) {
                texts[i][j].setOnLongClickListener {
                    val dialog = Dialog(this)
                    val bind = EditDialogBinding.inflate(layoutInflater)

                    dialog.setContentView(bind.root)
                    dialog.setCancelable(false)
                    dialog.show()
                    bind.etUpdate.setText(texts[i][j].text.toString())
                    bind.cancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    bind.done.setOnClickListener {
                        if (bind.etUpdate.text.toString().trim().isNotEmpty()) {
                            texts[i][j].text = bind.etUpdate.text.toString().trim()
                            isEdited = true
                            dialog.dismiss()
                        } else {
                            mess.toast("Cannot add empty item.")
                        }
                    }
                    true
                }
            }
        }
    }


    private fun getEditedMenu(onResult: (Menu) -> Unit) {
        var editedMenu: Menu
        val dayMenus: MutableList<DayMenu> = mutableListOf()
        dayMenus.add(DayMenu())
        val types = listOf("Breakfast", "Lunch", "Snacks", "Dinner")
        val timings = listOf(
            "8:30 AM to 10:00 AM",
            "12:30 PM to 2:30 PM",
            "5:00 PM to 6:00 PM",
            "7:30 PM to 9:30 PM"
        )
        for (i in 0..6) {
            val particulars = mutableListOf<Particulars>()
            for (j in 0..3) {
                particulars.add(
                    Particulars(
                        types[j],
                        texts[j][i].text.toString().trim(),
                        timings[j]
                    )
                )
            }
            dayMenus.add(DayMenu(particulars))
        }
        val user = mess.getUser()
        editedMenu = Menu(
            id = 1,
            creator = user,
            menu = dayMenus,


            )
        onResult(editedMenu)


    }

    override fun onBackPressed() {
        if (isEdited) {
            mess.showAlertDialog(
                "Alert!",
                "You have unsaved changes,Your data will be gone\nYou want to go back?",
                "Yes",
                "Cancel"
            ) {

                finish()
            }
        } else
            super.onBackPressed()
    }
}
