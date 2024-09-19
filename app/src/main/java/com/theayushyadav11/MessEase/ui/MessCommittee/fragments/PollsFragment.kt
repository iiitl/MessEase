package com.theayushyadav11.MessEase.ui.MessCommittee.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.databinding.FragmentPollsBinding
import com.theayushyadav11.MessEase.ui.Adapters.PollAdapter
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.PollsViewModel
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Mess

class PollsFragment : Fragment() {

    private lateinit var binding: FragmentPollsBinding
    private val viewModel: PollsViewModel by viewModels()
    private lateinit var mess:Mess

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPollsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        setListeners()

    }

    private fun initialise() {
        mess = Mess(requireContext())
       setAdapter()
    }

    private fun setListeners() {

    }
    private fun setAdapter() {
        if (isAdded) {
            val user=mess.getUser()

            viewModel.getMyPolls(user) { polls ->
                if (isAdded) {
                    if (polls.isEmpty()) {
                        binding.recyclerView.isVisible = false
                        binding.message.visibility = View.VISIBLE
                    } else {
                        binding.message.visibility = View.GONE
                        binding.recyclerView.isVisible = true
                        binding.recyclerView.adapter = PollAdapter(polls, requireContext())
                        binding.recyclerView.layoutManager = getmanager()
                    }
                }
            }
        }
    }

    fun getmanager(): RecyclerView.LayoutManager {
        val layoutManager = object : LinearLayoutManager(context) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {

                }
            }

            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        return layoutManager
    }
}