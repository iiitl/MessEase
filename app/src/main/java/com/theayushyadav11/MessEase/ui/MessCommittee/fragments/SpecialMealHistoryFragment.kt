package com.theayushyadav11.MessEase.ui.MessCommittee.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.theayushyadav11.MessEase.databinding.FragmentSpecialMealHistoryBinding
import com.theayushyadav11.MessEase.ui.Adapters.SpecialMealAdapter
import com.theayushyadav11.MessEase.utils.Mess

class SpecialMealHistoryFragment : Fragment() {


    private lateinit var binding: FragmentSpecialMealHistoryBinding
    private lateinit var mess: Mess
    private val viewModel: SpecialMealHistoryViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpecialMealHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        initialise()
        setUpRecyclerView()

    }

    private fun setUpToolBar() {
        val toolbar: Toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = "Special Meal History"
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initialise() {
        mess = Mess(requireContext())
        binding.refreshLayout.setOnRefreshListener{
            setUpRecyclerView()
            binding.refreshLayout.isRefreshing = false
        }
    }

    fun setUpRecyclerView() {
        mess.addPb("Loading History")
        viewModel.getHistory {
            if (it.isNotEmpty()&&isAdded) {
                binding.message.visibility = View.GONE
                binding.recyclerView.isVisible = true
                binding.recyclerView.apply {
                    adapter = SpecialMealAdapter(it,context)
                    layoutManager = LinearLayoutManager(requireContext())
                }
            } else {
                binding.message.isVisible = true
                binding.recyclerView.isVisible = false
            }
            mess.pbDismiss()
        }
    }
}