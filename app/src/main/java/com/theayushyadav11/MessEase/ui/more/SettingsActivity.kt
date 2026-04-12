package com.theayushyadav11.MessEase.ui.more

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.theayushyadav11.MessEase.databinding.ActivitySettingsBinding
import com.theayushyadav11.MessEase.utils.Mess
import java.util.Calendar

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySettingsBinding
    private lateinit var mess:Mess

    private val tempStates = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
       binding= ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mess=Mess(this)
        setUpToolBar()
        initialise()
        listeners()
        setupToggle("bt_enabled", binding.switchBreakfast, binding.timeb, binding.pickb)
        setupToggle("lt_enabled", binding.switchLunch, binding.timel, binding.pickl)
        setupToggle("st_enabled", binding.switchSnacks, binding.times, binding.picks)
        setupToggle("dt_enabled", binding.switchDinner, binding.timed, binding.pickd)
        tempStates["bt_enabled"] = mess.isMealEnabled("bt_enabled")
        tempStates["lt_enabled"] = mess.isMealEnabled("lt_enabled")
        tempStates["st_enabled"] = mess.isMealEnabled("st_enabled")
        tempStates["dt_enabled"] = mess.isMealEnabled("dt_enabled")

    }

    private fun initialise() {
        val times = listOf(mess.get("bt","7:30"),mess.get("lt","12:0"),mess.get("st","16:30"),mess.get("dt","19:0"))
        binding.timeb.text=getFormattedTime(times[0])
        binding.timel.text=getFormattedTime(times[1])
        binding.times.text=getFormattedTime(times[2])
        binding.timed.text=getFormattedTime(times[3])
    }

    fun listeners()
    {
        val b = mutableListOf(
            mess.get("bt","7:30"),
            mess.get("lt","12:0"),
            mess.get("st","16:30"),
            mess.get("dt","19:0")
        )
        binding.pickb.setOnClickListener{
            showTimePicker(0, 0, 10, 0) {it,p->
                binding.timeb.text = it
               b[0]=(p)
            }
        }
        binding.pickl.setOnClickListener{
            showTimePicker(10, 0, 14, 30) {it,p->
                binding.timel.text = it
                b[1]=(p)
            }
        }
        binding.picks.setOnClickListener{
            showTimePicker(14, 30, 18, 0) {it,p->

                binding.times.text = it
                b[2]=(p)
            }
        }
        binding.pickd.setOnClickListener{
            showTimePicker(18, 0, 21, 30) {it,p->
                binding.timed.text = it
                b[3]=(p)
            }
        }
        binding.done.setOnClickListener{

            mess.save("bt",b[0])
            mess.save("lt",b[1])
            mess.save("st",b[2])
            mess.save("dt",b[3])
            mess.log(b)
            //cancelAllAlarms(this@SettingsActivity)
            mess.setMealEnabled("bt_enabled", tempStates["bt_enabled"]!!)
            mess.setMealEnabled("lt_enabled", tempStates["lt_enabled"]!!)
            mess.setMealEnabled("st_enabled", tempStates["st_enabled"]!!)
            mess.setMealEnabled("dt_enabled", tempStates["dt_enabled"]!!)

            mess.toast("Settings Updated")
            finish()
        }

    }
    private fun setUpToolBar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Settings"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun showTimePicker(minHour: Int, minMinute: Int, maxHour: Int, maxMinute: Int, onResult: (String, String) -> Unit) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
            if (isTimeInRange(hourOfDay, minute, minHour, minMinute, maxHour, maxMinute)) {

                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                val timeFormat = android.text.format.DateFormat.getTimeFormat(this)
                val formattedTime = timeFormat.format(selectedTime.time)

                onResult(formattedTime,"$hourOfDay:$minute")
            } else {
                Toast.makeText(this, "Please choose between ${getFormattedTime("$minHour:$minMinute")} and  ${getFormattedTime("$maxHour:$maxMinute")}", Toast.LENGTH_SHORT).show()
            }
        }, currentHour, currentMinute, false)

        timePickerDialog.show()
    }


    private fun isTimeInRange(hour: Int, minute: Int, minHour: Int, minMinute: Int, maxHour: Int, maxMinute: Int): Boolean {
        val selectedTime = hour * 60 + minute
        val minTime = minHour * 60 + minMinute
        val maxTime = maxHour * 60 + maxMinute
        return selectedTime in minTime..maxTime
    }
    private fun getFormattedTime(time:String): String {
        val hour = time.substringBefore(":").toInt()
        val minute = time.substringAfter(":").toInt()

        val selectedTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val timeFormat = android.text.format.DateFormat.getTimeFormat(this)
        val formattedTime = timeFormat.format(selectedTime.time)
        return formattedTime
    }
    private fun setupToggle(
        key: String,
        switch: Switch,
        timeView: TextView,
        picker: View
    ) {
        val isEnabled = mess.isMealEnabled(key)
        switch.isChecked = isEnabled

        updateUIState(isEnabled, timeView, picker)

        switch.setOnCheckedChangeListener { _, checked ->
            tempStates[key] = checked
            updateUIState(checked, timeView, picker)
        }
    }

    private fun updateUIState(
        isEnabled: Boolean,
        timeView: TextView,
        picker: View
    ) {
        timeView.alpha = if (isEnabled) 1f else 0.4f
        picker.alpha = if (isEnabled) 1f else 0.4f
        picker.isEnabled = isEnabled
    }



}