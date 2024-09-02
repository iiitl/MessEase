package com.theayushyadav11.MessEase.ui.more

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theayushyadav11.MessEase.Models.DayMenu
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDatabase
import com.theayushyadav11.MessEase.databinding.ActivityShowMenuBinding
import com.theayushyadav11.MessEase.utils.Constants.Companion.authority
import com.theayushyadav11.MessEase.utils.Constants.Companion.menuFileName
import com.theayushyadav11.MessEase.utils.Constants.Companion.menuFileType
import com.theayushyadav11.MessEase.utils.Mess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ShowMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowMenuBinding
    private var texts: MutableList<MutableList<TextView>> = mutableListOf()
    private lateinit var mess: Mess
    private var isClicked = false
    private lateinit var uri: Uri
    private lateinit var currentMenu: Menu
    private var isEdited = false
    val REQUEST_CODE = 1232
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityShowMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        askPermissions()
        initialise()
        assign()
    }

    private fun initialise() {
        mess = Mess(this)
        mess.addPb("Loading menu...")
        getShowingMenu { menu ->
            currentMenu = menu
            assign()
            setFood(menu.menu)
            pdfConversion()
            initFilePaths()
            openPDF()
            finish()




            mess.pbDismiss()
        }
    }

    fun getShowingMenu(onResult: (Menu) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val menu = MenuDatabase.getDatabase(this@ShowMenuActivity).menuDao().getShowMenu()
            onResult(menu)
        }
    }

    private fun askPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), REQUEST_CODE
            )
        } else {

        }
    }

    fun initFilePaths() {
        val fileName = menuFileName
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        if (file.exists()) {
            uri = FileProvider.getUriForFile(
                this, authority, file
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            pdfConversion()

        }

        isClicked = false
    }

    private fun setFood(menu: List<DayMenu>) {
        for (i in 0..3) {
            for (j in 0..6) {
                texts[i][j].text = menu[j + 1].particulars[i].food
            }
        }
    }

    private fun pdfConversion() {
        try {
            val horizontalScrollView = binding.root
            val displayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this.display?.getRealMetrics(displayMetrics)
            } else {
                @Suppress("DEPRECATION") this.windowManager.defaultDisplay.getMetrics(displayMetrics)
            }
            horizontalScrollView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            horizontalScrollView.layout(
                0, 0, horizontalScrollView.measuredWidth, horizontalScrollView.measuredHeight
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
        } catch (e: Exception) {
            e.printStackTrace()
            mess.log("An error occurred while creating the PDF: ${e.message}")
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


}