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
import com.theayushyadav11.MessEase.databinding.FragmentUploadMenuBinding
import com.theayushyadav11.MessEase.ui.Adapters.UploadMenuAdapter
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.UploadMenuViewModel

class UploadMenuFragment : Fragment(){

    private lateinit var binding: FragmentUploadMenuBinding
    private val viewModel: UploadMenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadMenuBinding.inflate(inflater, container, false)
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
        toolbar.title = "Upload Menu"
        toolbar.setTitleTextColor(Color.WHITE)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setAdapter() {
        viewModel.getAprMenus(onSuccess={ aprMenus->
            if(isAdded) {
               if(aprMenus.isEmpty()) {
                   binding.message.visibility = View.VISIBLE
                   binding.rv.visibility = View.GONE
                   return@getAprMenus
               }
                binding.message.visibility = View.GONE
                binding.rv.visibility = View.VISIBLE
                var adapter = UploadMenuAdapter(aprMenus, requireContext())
                binding.rv.apply {
                    this.adapter = adapter
                    this.layoutManager = LinearLayoutManager(requireContext())
                }

            }
        }, onFailure = {
            binding.message.visibility = View.VISIBLE
            binding.message.text ="Something went wrong"
        })

    }
}