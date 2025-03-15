package com.theayushyadav11.MessEase.ui.MessCommittee.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.theayushyadav11.MessEase.databinding.FragmentReviewBinding
import com.theayushyadav11.MessEase.ui.Adapters.ReviewAdapter
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.ReviewViewModel

class ReviewFragment : Fragment() {

    private lateinit var binding: FragmentReviewBinding
    private val viewModel: ReviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        setListeners()

    }

    private fun initialise() {
        setToolBar()
        setAdapter()
    }

    private fun setListeners() {

    }

    private fun setToolBar() {
        val toolbar: Toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = "Reviews"
        toolbar.setTitleTextColor(Color.WHITE)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

    }

    private fun setAdapter() {
        viewModel.getAllReviews { reviews ->
            if (isAdded) {
                if (reviews.isEmpty()) {
                    binding.message.visibility = View.VISIBLE
                    binding.rv.visibility = View.GONE
                    return@getAllReviews
                }
                if (isAdded) {
                    val adapter = ReviewAdapter(reviews, requireContext())
                    binding.rv.layoutManager = LinearLayoutManager(requireContext())
                    binding.rv.adapter = adapter
                }

            }
        }

    }
}