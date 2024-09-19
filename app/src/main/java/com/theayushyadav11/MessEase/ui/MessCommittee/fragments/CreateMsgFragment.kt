package com.theayushyadav11.MessEase.ui.MessCommittee.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.FragmentCreateMsgBinding
import com.theayushyadav11.MessEase.notifications.PushNotifications
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.CreateMsgViewModel
import com.theayushyadav11.MessEase.utils.Constants.Companion.compressImage
import com.theayushyadav11.MessEase.utils.Mess

class CreateMsgFragment : Fragment() {
    private lateinit var binding: FragmentCreateMsgBinding
    private val viewModel: CreateMsgViewModel by viewModels()
    val PICK_IMAGE_REQUEST = 1
    val listOfImages: MutableList<Uri> = mutableListOf()
    var noi = 0
    private lateinit var mess: Mess
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateMsgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        setListeners()

    }

    private fun initialise() {
        mess = Mess(requireContext())
        setToolBar()
        onBackPressed()
    }

    private fun setListeners() {
        binding.addImage.setOnClickListener {
            openFileChooser()
        }
        binding.btnPost.setOnClickListener {
            if (binding.tvQuestion.text.isNotEmpty() && binding.tvBody.text.isNotEmpty()) {
                addmsg()
            } else {
                mess.snack(binding.btnPost, "Cannot add Empty feilds!")
            }
        }


    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data

            if (noi < 3) {
                val view = LayoutInflater.from(context).inflate(R.layout.img, binding.adder, false)
                val img = view.findViewById<ImageView>(R.id.img)



                Glide.with(this)
                    .load(imageUri)
                    .into(img)

                binding.adder.addView(view)
                if (imageUri != null) {
                    listOfImages.add(imageUri)
                }
                view.setOnLongClickListener {
                    mess.showAlertDialog(
                        "Alert!",
                        "Do you want to remove this image?",
                        "Yes",
                        "No"
                    ) {
                        binding.adder.removeView(view)
                        listOfImages.remove(imageUri)
                        binding.addImage.visibility = View.VISIBLE
                        noi--
                    }

                    true
                }
                noi++;

            }
            if (noi == 3) {
                binding.addImage.visibility = View.GONE
            }


        }
    }

    private fun addmsg() {
        var listOfImg = mutableListOf<ByteArray>()
        for (img in listOfImages) {
            if (isAdded) {
                val byteArray = compressImage(requireContext(), img)
                listOfImg.add(byteArray)
            }

        }


        mess.openDialog("Msg") { target ->
            mess.addPb("Adding Msg..")
            viewModel.addMsg(
                title = binding.tvQuestion.text.toString(),
                body = binding.tvBody.text.toString(),
                photos = listOfImg,
                user = mess.getUser(),
                target = target,
                onSuccess = {
                    if (isAdded)
                        findNavController().navigateUp()
                    mess.pbDismiss()
                    mess.toast("Msg Added Successfully")
                    if (isAdded) {
                        val pn = PushNotifications(requireContext(), target)
                        pn.sendNotificationToAllUsers(
                            "New Message Added",
                            binding.tvQuestion.text.toString()
                        )
                    }
                },
                onFailure = {
                    mess.pbDismiss()
                    mess.toast("Failed to add Msg")
                }
            )
        }
    }

    private fun setToolBar() {
        val toolbar: Toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = "Create Message"

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            if (binding.tvQuestion.text.isNotEmpty() || binding.tvBody.text.isNotEmpty() || listOfImages.isNotEmpty()) {
                mess.showAlertDialog("Alert!", "Do you want to discard the Message?", "Yes", "No") {
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
                    if (binding.tvQuestion.text.isNotEmpty() || binding.tvBody.text.isNotEmpty() || listOfImages.isNotEmpty()) {
                        mess.showAlertDialog(
                            "Alert!",
                            "Do you want to discard the Message?",
                            "Yes",
                            "No"
                        ) {
                            findNavController().navigateUp()
                        }
                    } else
                        findNavController().navigateUp()
                }
            })
    }

}