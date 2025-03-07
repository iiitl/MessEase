package com.theayushyadav11.MessEase.ui.NavigationDrawers.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.FragmentKnowOurTeamBinding
import com.theayushyadav11.MessEase.ui.NavigationDrawers.ViewModels.SlideshowViewModel
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Mess

class KnowOurTeamFragment : Fragment() {
    private lateinit var binding: FragmentKnowOurTeamBinding
    private val viewModel: SlideshowViewModel by viewModels()
    private lateinit var mess: Mess
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentKnowOurTeamBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        listeners()

    }

    private fun initialise() {
        mess = Mess(requireContext())
        addCoord()
    }

    private fun listeners() {

    }

    fun addCoord() {
        viewModel.getCoord { users ->
            binding.coordAdder.removeAllViews()
            binding.devAdder.removeAllViews()
            binding.smAdder.removeAllViews()
            binding.memAdder.removeAllViews()
            binding.volAdder.removeAllViews()
            for (user in users) {
                if (isAdded) {
                    val v = LayoutInflater.from(requireContext()).inflate(R.layout.person, null)
                    v.findViewById<TextView>(R.id.mname).text = user.name
                    v.findViewById<TextView>(R.id.email).text = user.email
                    mess.loadCircleImage(user.photoUrl, v.findViewById(R.id.profilePhoto))
                    val user=mess.getUser()
                        if (user.designation == "Coordinator"||user.designation=="Developer") {
                            v.findViewById<ImageView>(R.id.delete).visibility = View.VISIBLE
                            v.findViewById<ImageView>(R.id.delete).setOnClickListener {
                                mess.showAlertDialog(
                                    "Confirm!",
                                    "Are you sure you want to delete this user?",
                                    "Delete",
                                    "Cancel"
                                ) {
                                    viewModel.delete(user.uid) {
                                        mess.toast(it)
                                    }
                                }

                            }
                        }

                  mess.log("User: ${user.name} - ${user.designation}")
                    when (user.designation) {
                        "Coordinator" -> binding.coordAdder.addView(v)
                        "Developer" -> binding.devAdder.addView(v)
                        "Senior-Member" -> binding.smAdder.addView(v)
                        "Member" -> binding.memAdder.addView(v)
                        "Volunteer" -> binding.volAdder.addView(v)
                    }

                }
            }

        }


    }


}