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

    private lateinit var binding: ActivityMenuBinding
    private val menuTextViews: MutableList<MutableList<TextView>> = mutableListOf()
    private lateinit var mess: Mess
    private var isNextButtonBlocked = false
    private lateinit var currentMenu: Menu
    private var isMenuEdited = false
    private val viewModel: EditMenuViewModel by viewModels {
        val database = MenuDatabase.getDatabase(this)
        EditMenuViewModelFactory(database.menuDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mess = Mess(this)
        initializeMenuView()
        requestStoragePermissions()
        loadInitialMenu()
        setupEditMenuListeners()
    }

    private fun initializeMenuView() {
        if (menuTextViews.isNotEmpty()) return

        val breakfastViews = listOf(
            binding.sube, binding.mobe, binding.tube, binding.webe, binding.thbe, binding.frbe, binding.sabe
        )
        menuTextViews.add(breakfastViews.toMutableList())

        val lunchViews = listOf(
            binding.sulu, binding.molu, binding.tulu, binding.welu, binding.thlu, binding.frlu, binding.salu
        )
        menuTextViews.add(lunchViews.toMutableList())

        val snacksViews = listOf(
            binding.susn, binding.mosn, binding.tusn, binding.wesn, binding.thsn, binding.frsn, binding.sasn
        )
        menuTextViews.add(snacksViews.toMutableList())

        val dinnerViews = listOf(
            binding.sudi, binding.modi, binding.tudi, binding.wedi, binding.thdi, binding.frdi, binding.sadi
        )
        menuTextViews.add(dinnerViews.toMutableList())
    }

    private fun loadInitialMenu() {
        mess.addPb("Loading menu...")
        viewModel.getMenuCurrentMenu { menu ->
            currentMenu = menu
            viewModel.setCurrentMenu(menu)
            populateMenu(menu.menu)
            mess.pbDismiss()
        }
    }

    private fun requestStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                WRITE_STORAGE_PERMISSION_CODE
            )
        } else {
            setupNextButtonListener()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_STORAGE_PERMISSION_CODE) {
            setupNextButtonListener()
        }
    }

    private fun populateMenu(menu: List<DayMenu>) {
        for (i in 0..3) { // Meal types
            for (j in 0..6) { // Days
                menuTextViews[i][j].text = menu[j + 1].particulars[i].food
            }
        }
    }

    private fun setupNextButtonListener() {
        binding.next.setOnClickListener {
            if (isNextButtonBlocked) return@setOnClickListener
            isNextButtonBlocked = true

            binding.next.isVisible = false
            generateMenuPdf()
            binding.next.isVisible = true

            val editedMenu = buildEditedMenu()
            mess.log("Menu: $editedMenu")
            viewModel.setEditedMenu(editedMenu)
            
            startActivity(Intent(this@EditMenuActivity, EditCompleteActivity::class.java))
            isNextButtonBlocked = false
        }
    }

    private fun generateMenuPdf() {
        val view = binding.root
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }

        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(view.measuredWidth, view.measuredHeight, 1).create()
        val page = document.startPage(pageInfo)
        
        view.draw(page.canvas)
        document.finishPage(page)

        val fileName = "Mess Menu.pdf"
        val filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            filePath.parentFile?.mkdirs()
            FileOutputStream(filePath).use { fos ->
                document.writeTo(fos)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            document.close()
        }
    }

    private fun setupEditMenuListeners() {
        for (i in menuTextViews.indices) {
            for (j in menuTextViews[i].indices) {
                menuTextViews[i][j].setOnLongClickListener {
                    showEditDialog(menuTextViews[i][j])
                    true
                }
            }
        }
    }

    private fun showEditDialog(textView: TextView) {
        val dialog = Dialog(this)
        val dialogBinding = EditDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        
        dialogBinding.etUpdate.setText(textView.text.toString())
        
        dialogBinding.cancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialogBinding.done.setOnClickListener {
            val updatedText = dialogBinding.etUpdate.text.toString().trim()
            if (updatedText.isNotEmpty()) {
                textView.text = updatedText
                isMenuEdited = true
                dialog.dismiss()
            } else {
                mess.toast("Cannot add empty item.")
            }
        }
        
        dialog.show()
    }

    private fun buildEditedMenu(): Menu {
        val dayMenus: MutableList<DayMenu> = mutableListOf()
        dayMenus.add(DayMenu()) // Assuming the first entry is a placeholder

        for (i in 0..6) { // Days
            val particulars = mutableListOf<Particulars>()
            for (j in 0..3) { // Meal types
                particulars.add(
                    Particulars(
                        MEAL_TYPES[j],
                        menuTextViews[j][i].text.toString().trim(),
                        MEAL_TIMINGS[j]
                    )
                )
            }
            dayMenus.add(DayMenu(particulars))
        }

        return Menu(
            id = 1,
            creator = mess.getUser(),
            menu = dayMenus
        )
    }

    override fun onBackPressed() {
        if (isMenuEdited) {
            mess.showAlertDialog(
                title = "Alert!",
                message = "You have unsaved changes. Your data will be lost. Do you want to go back?",
                okText = "Yes",
                cancelText = "Cancel"
            ) {
                finish()
            }
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val WRITE_STORAGE_PERMISSION_CODE = 1232
        private val MEAL_TYPES = listOf("Breakfast", "Lunch", "Snacks", "Dinner")
        private val MEAL_TIMINGS = listOf(
            "8:30 AM to 10:00 AM",
            "12:30 PM to 2:30 PM",
            "5:00 PM to 6:00 PM",
            "7:30 PM to 9:30 PM"
        )
    }
}
