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

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var mess: Mess
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialise()
        listeners()
    }

    private fun initialise() {
        mess = Mess(requireContext())
        setAdapter()
    }

    private fun listeners() {
        binding.btnAdd.setOnClickListener {
            validateAndAdd()
        }
    }

    private fun validateAndAdd() {
        val email = binding.etEmail.text.toString().trim()
        val designation = binding.spinnerAutoComplete.text.toString().trim()
        binding.tilEmail.error = null
        binding.tilspin.error = null

        var isValid = true
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Invalid email"
            isValid = false
        }

        if (designation.isEmpty()) {
            binding.tilspin.error = "Select designation"
            isValid = false
        }

        if (isValid) {
            add(email, designation)
        }
    }

    private fun add(email: String, designation: String) {
        _binding?.let { binding ->
            binding.btnAdd.isEnabled = false
            binding.btnAdd.text = "Adding..."
        }

        mess.addPb("Adding to Mess Committee")

        viewModel.addToMessCommittee(email, designation) { result ->
            mess.pbDismiss()
            val binding = _binding
            if (binding != null) {
                binding.btnAdd.isEnabled = true
                binding.btnAdd.text = "Add to Committee"

            }
            mess.toast(result)
            if (result.contains("success", ignoreCase = true)) {
                binding?.etEmail?.text?.clear()
                binding?.spinnerAutoComplete?.text?.clear()
            }
        }
    }

    private fun setAdapter() {
        mess.getLists("${DESIGNATION}s") {list ->
            if (!isAdded || context == null) return@getLists
            val b = _binding ?: return@getLists
            val ctx = context ?: return@getLists
            val adapter = ArrayAdapter(
                ctx,
                android.R.layout.simple_dropdown_item_1line,
                list
            )
            b.spinnerAutoComplete.setAdapter(adapter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}