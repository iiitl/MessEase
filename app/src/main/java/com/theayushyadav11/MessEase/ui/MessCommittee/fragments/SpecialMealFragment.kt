package com.theayushyadav11.MessEase.ui.MessCommittee.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.FragmentSpecialMealBinding
import com.theayushyadav11.MessEase.notifications.PushNotifications
import com.theayushyadav11.MessEase.utils.Constants.Companion.ALL_USERS
import com.theayushyadav11.MessEase.utils.Mess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SpecialMealFragment : Fragment() {

    private val viewModel: SpecialMealViewModel by viewModels()
    private lateinit var binding: FragmentSpecialMealBinding
    private lateinit var mess: Mess
    private var selectedDate: String = ""
    val mealTypes = arrayOf("Breakfast", "Lunch", "Snacks", "Dinner")
    var mealType = ""
    var mealDetails = ""
    var date = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpecialMealBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        setupListeners()
    }

    private fun initialise() {
        mess = Mess(requireContext())
        setToolBar()
        setupMealTypeDropdown()
    }

    private fun setupListeners() {
        binding.dateEditText.setOnClickListener {
            showDatePicker()
        }

        binding.uploadMenuButton.setOnClickListener {
            validateAndUpload()
        }
    }

    private fun setToolBar() {
        val toolbar: Toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = "Special Meal"
        toolbar.setTitleTextColor(Color.WHITE)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupMealTypeDropdown() {

        val adapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, mealTypes)
        (binding.mealTypeDropdown as? AutoCompleteTextView)?.setAdapter(adapter)

    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(childFragmentManager, "MATERIAL_DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDateObj = Date(selection)
            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            selectedDate = format.format(selectedDateObj)
            binding.dateEditText.setText(selectedDate)
            viewModel.day.value = selectedDateObj.date
            viewModel.month.value = selectedDateObj.month
            viewModel.year.value = selectedDateObj.year
        }
    }

    private fun validateAndUpload() {
        date = binding.dateEditText.text.toString()
        mealType = binding.mealTypeDropdown.text.toString()
        mealDetails = binding.mealDetailsEditText.text.toString()

        if (date.isEmpty()) {
            binding.dateLayout.error = "Please select a date"
            return
        }

        if (mealType.isEmpty()) {
            binding.mealTypeLayout.error = "Please select a meal type"
            return
        }

        if (mealDetails.isEmpty()) {
            binding.mealDetailsLayout.error = "Please enter meal details"
            return
        }

        binding.dateLayout.error = null
        binding.mealTypeLayout.error = null
        binding.mealDetailsLayout.error = null
        viewModel.mealName.value = mealDetails
        viewModel.mealIndex.value = mealTypes.indexOf(mealType)
        binding.uploadMenuButton.isEnabled = false
        binding.uploadMenuButton.text = "Uploading..."
        mess.showAlertDialog("Alert!","Are you sure you want to Upload Special $mealType?","Upload","Cancel"){
            uploadSpecialMeal()

        }



    }

    private fun uploadSpecialMeal() {
        mess.addPb("Adding Special Meal")
        binding.uploadMenuButton.isEnabled = true
        binding.uploadMenuButton.text = "Upload Special Meal"
        viewModel.uploadSpecialMeal {
            mess.pbDismiss()
            mess.toast("Special meal details uploaded successfully!")
            binding.mealDetailsEditText.text?.clear()
            binding.mealTypeDropdown.text?.clear()
            binding.dateEditText.text?.clear()
            //sendNotification()

        }

    }

    private fun sendNotification() {
        val pn = PushNotifications(requireContext(), ALL_USERS)
        pn.sendNotificationToAllUsers(
            "New Special $mealType added.",
            "New Special $mealType added. \non $date \n Menu: $mealDetails",
        )
    }
}