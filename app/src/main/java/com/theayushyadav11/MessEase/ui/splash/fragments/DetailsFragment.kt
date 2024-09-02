package com.theayushyadav11.MessEase.ui.splash.fragments

import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import com.theayushyadav11.MessEase.MainActivity
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.FragmentDetailsBinding
import com.theayushyadav11.MessEase.databinding.FragmentHomeBinding
import com.theayushyadav11.MessEase.ui.NavigationDrawers.ViewModels.HomeViewModel
import com.theayushyadav11.MessEase.ui.splash.ViewModels.DetailsViewModel
import com.theayushyadav11.MessEase.utils.Mess
import java.util.Date

class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var mess: Mess
    private val viewModel: DetailsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        setToolBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mess= Mess(requireContext())
        addAdapter()
        disableBackButton()
        addDetails()

    }

    private fun disableBackButton() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun addAdapter() {
        val year = Date().year + 1900
        var listOfYear = listOf(year, year + 1, year + 2, year + 3, year + 4, year + 5)
        val adapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, listOfYear)
        binding.auto.setAdapter(adapter)


        var listOfBatch = listOf("Btech", "Mtech", "M.B.A.", "MSc", "Phd")
        val Batchadapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, listOfBatch)
        binding.autoBatch.setAdapter(Batchadapter)

        val adapter2 =
            ArrayAdapter(requireContext(), R.layout.drop_down_item, listOf("Male", "Female"))
        binding.autoGender.setAdapter(adapter2)

    }
    private fun addDetails() {


        binding.done.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val passingYear = binding.auto.text.toString().trim()
            val gender = binding.autoGender.text.toString().trim()
            val batch = binding.autoBatch.text.toString().trim()
            if (name.isNotEmpty()) {
                mess.addPb("Adding Details...")
                viewModel.addDetails(name, batch,passingYear,gender,
                    onSuccess = {
                        mess.toast("Details Added Successfully")
                        mess.pbDismiss()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    },

                onFailure = {
                    mess.pbDismiss()
                    mess.toast(it.message.toString())
                })
            } else {
                mess.toast("Name cannot be empty!")
            }
        }

    }
    private fun setToolBar() {
        val toolbar: Toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = "Enter Your Details"



    }

}