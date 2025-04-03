package com.theayushyadav11.MessEase.ui.NavigationDrawers.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theayushyadav11.MessEase.databinding.FragmentAdminBinding
import com.theayushyadav11.MessEase.ui.NavigationDrawers.ViewModels.AdminViewModel
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESIGNATION
import com.theayushyadav11.MessEase.utils.Mess

class AdminFragment : Fragment() {
    private lateinit var binding: FragmentAdminBinding
    private lateinit var mess: Mess

    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        listeners()
    }

    private fun listeners() {
        binding.btnAdd.setOnClickListener {
            add()
        }
    }

    private fun initialise() {
        mess = Mess(requireContext())
        setAdapter()
    }


    fun add() {
        if (binding.etEmail.text.toString().isNotEmpty()) {
            mess.addPb("Adding to Mess Committee")
            viewModel.addToMessCommittee(
                binding.etEmail.text.toString(),
                binding.spinnerAutoComplete.text.toString()
            )
            {
                mess.pbDismiss()
                mess.toast(it)
            }
        } else {
            mess.toast("Please enter email")
        }
    }

    private fun setAdapter() {
        mess.getLists("${DESIGNATION}s") {
            val spinner = binding.spinnerAutoComplete
            val spinnerItems = it
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setAdapter(adapter)
        }
    }
}