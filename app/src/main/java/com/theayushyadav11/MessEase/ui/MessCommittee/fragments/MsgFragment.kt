package com.theayushyadav11.MessEase.ui.MessCommittee.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theayushyadav11.MessEase.databinding.FragmentMsgBinding
import com.theayushyadav11.MessEase.ui.Adapters.MsgAdapter
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.MsgViewModel
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Mess

class MsgFragment : Fragment() {

    private lateinit var binding: FragmentMsgBinding
    private val viewModel: MsgViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMsgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        setListeners()

    }

    private fun initialise() {
        setAdapter()
    }

    private fun setListeners() {

    }

    fun setAdapter() {
        if (isAdded) {
            val uid = auth.currentUser?.uid.toString()
            viewModel.getMyMsgs(uid, Mess(requireContext()).getUser()) { msgs ->
                if (isAdded) {
                    if (msgs.isEmpty()) {
                        binding.recyclerView.isVisible = false
                        binding.message.visibility = View.VISIBLE
                    } else {
                        binding.message.visibility = View.GONE
                        binding.recyclerView.isVisible = true
                        if (isAdded)
                            binding.recyclerView.adapter = MsgAdapter(msgs, requireContext())
                        binding.recyclerView.layoutManager = getmanager()
                    }
                }
            }
        }
    }

    fun getmanager(): RecyclerView.LayoutManager {
        val layoutManager = object : LinearLayoutManager(context) {
            override fun onLayoutChildren(
                recycler: RecyclerView.Recycler?,
                state: RecyclerView.State?
            ) {
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