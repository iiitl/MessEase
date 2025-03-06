package com.theayushyadav11.MessEase.ui.MessCommittee.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.FragmentViewVotesBinding
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.ViewVotesViewModel
import com.theayushyadav11.MessEase.utils.Mess

class ViewVotesFragment : Fragment() {

    private lateinit var binding: FragmentViewVotesBinding
    private val viewModel: ViewVotesViewModel by viewModels()
    private lateinit var mess: Mess

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewVotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        setListeners()

    }

    private fun setListeners() {

    }

    private fun initialise() {
        mess = Mess(requireContext())
        setToolBar()
        mess.addPb("Loading...")
        try {

            viewModel.getPoll(mess.getPollId()) {
                val poll = it
                binding.tvQuestion.text = it.question
                binding.adder.removeAllViews()
                for (option in poll.options) {
                    addOption(poll.id, option)
                }


            }
        } catch (e: Exception) {
            mess.pbDismiss()
            mess.toast("Failed to load")
        }
    }

    private fun setToolBar() {
        val toolbar: Toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = "Poll Details"

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun addOption(id: String, opt: String) {
        try {
            val option = LayoutInflater.from(requireContext())
                .inflate(R.layout.poll_detail_layout, binding.adder, false)
            val optTitle = option.findViewById<TextView>(R.id.tvOpt)
            val nov = option.findViewById<TextView>(R.id.nov)
            val adder = option.findViewById<LinearLayout>(R.id.adder)
            optTitle.text = opt
            viewModel.getoptionDetails(id, opt, onSuccess = { votes, os ->
                nov.text = "$votes Votes"
                adder.removeAllViews()
                for (i in 0 until os.size) {
                    try {
                        val usr = LayoutInflater.from(requireContext())
                            .inflate(R.layout.name_time, adder, false)
                        val name = usr.findViewById<TextView>(R.id.mname)
                        val time = usr.findViewById<TextView>(R.id.time)
                        val propic = usr.findViewById<ImageView>(R.id.propic)
                        mess.loadCircularImage(os[i].user.photoUrl, propic)
                        val email = usr.findViewById<TextView>(R.id.email)
                        name.text = os[i].user.name
                        email.text = os[i].user.email
                        time.text = os[i].time + " " + os[i].date
                        adder.addView(usr)
                    } catch (e: Exception) {

                    }
                    mess.pbDismiss()
                }
                mess.pbDismiss()


            }, onFailure = {
                mess.pbDismiss()
            })



            binding.adder.addView(option)
        } catch (e: Exception) {
            mess.pbDismiss()
        }

    }


}