package com.theayushyadav11.MessEase.ui.MessCommittee.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.FragmentCreatePollBinding
import com.theayushyadav11.MessEase.notifications.PushNotifications
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.CreatePollViewModel
import com.theayushyadav11.MessEase.utils.Mess

class CreatePollFragment : Fragment() {
    var optionList: MutableList<EditText> = mutableListOf()
    private lateinit var mess: Mess
    private lateinit var binding: FragmentCreatePollBinding
    private val viewModel: CreatePollViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePollBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        setListeners()

    }

    private fun initialise() {
        optionList.add(binding.opt0)
        optionList.add(binding.opt1)
        mess = Mess(requireContext())
        setToolBar()
        onBackPressed()

    }

    private fun setListeners() {
        addElements(binding.opt1)
        binding.btnPost.setOnClickListener {
            var options: MutableSet<String> = mutableSetOf()
            for (i in optionList) {

                if (i.text.toString().isNotEmpty())
                    options.add(i.text.toString() + "\n")
            }
            if (binding.tvQuestion.text.isNotEmpty() && options.size > 1)
                mess.openDialog("Poll") { target ->
                    addPoll(target, options.toMutableList())
                }
            else {
                mess.toast("Cannot add Empty feilds!")
            }

        }

    }

    private fun addPoll(target: String, options: MutableList<String>) {
        mess.addPb("Adding Poll")
        viewModel.addPoll(
            binding.tvQuestion.text.toString(),
            target,
            options,
            onSuccess = {
                mess.pbDismiss()
                mess.toast("Poll Added Successfully")
                if (isAdded) {
                    val pn= PushNotifications(requireContext(), target)
                    pn.sendNotificationToAllUsers("New Poll Added\n Vote now!", binding.tvQuestion.text.toString())
                }
                findNavController().navigateUp()
            },
            onFailure = {
                mess.pbDismiss()
                mess.toast("Failed to add Poll")
            }
        )
    }

    fun addElements(edit: EditText) {
        edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! < 2) {
                    val adding = LayoutInflater.from(binding.adder.context)
                        .inflate(R.layout.poll_input_elements, binding.adder, false)
                    val et: EditText = adding.findViewById<EditText>(R.id.opt)
                    optionList.add(et)
                    addElements(et)
                    binding.adder.addView(adding)
                    binding.scroll.post {
                        binding.scroll.smoothScrollTo(0, et.bottom)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setToolBar() {
        val toolbar: Toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = "Create Poll"

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            if (binding.tvQuestion.text.isNotEmpty() || binding.opt0.text.isNotEmpty() || binding.opt1.text.isNotEmpty()) {
                mess.showAlertDialog("Alert!", "Do you want to discard the Poll?", "Yes", "No") {
                    findNavController().navigateUp()
                }
            } else
                findNavController().navigateUp()
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.tvQuestion.text.isNotEmpty() || binding.opt0.text.isNotEmpty() || binding.opt1.text.isNotEmpty()) {
                        mess.showAlertDialog("Alert!", "Do you want to discard the Poll?", "Yes", "No") {
                          findNavController().navigateUp()
                        }
                    } else
                        findNavController().navigateUp()
                }
            })
    }
}