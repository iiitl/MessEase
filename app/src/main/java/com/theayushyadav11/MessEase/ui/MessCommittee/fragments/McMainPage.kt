package com.theayushyadav11.MessEase.ui.MessCommittee.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.theayushyadav11.MessEase.MainActivity
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.R.id.action_mcMainPage_to_createMsgFragment
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDatabase
import com.theayushyadav11.MessEase.databinding.FragmentMcMainPageBinding
import com.theayushyadav11.MessEase.ui.Adapters.ViewPagerAdapter
import com.theayushyadav11.MessEase.ui.MessCommittee.activities.EditMenuActivity
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.McMainPageViewModel
import com.theayushyadav11.MessEase.utils.Constants.Companion.COORDINATOR
import com.theayushyadav11.MessEase.utils.Constants.Companion.DEVELOPER
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Mess
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class McMainPage : Fragment() {
    private lateinit var binding: FragmentMcMainPageBinding
    private lateinit var mess: Mess

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMcMainPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        setListeners()
    }

    private fun initialise() {
        mess = Mess(requireContext())
        setValues()
        view?.post {
            setTab()
        }
    }

    private fun setListeners() {
        binding.btnPoll.setOnClickListener {
            navigateSafely(R.id.action_mcMainPage_to_createPollFragment)
        }
        binding.createMsg.setOnClickListener {
            navigateSafely(action_mcMainPage_to_createMsgFragment)
        }
        binding.editMenu.setOnClickListener {
            val db=MenuDatabase.getDatabase(requireContext()).menuDao()
            GlobalScope.launch {
                val currentMenu=db.getMenu()
                val menuTo= Menu(id=3, menu = currentMenu.menu, creator = currentMenu.creator)
                db.addMenu(menuTo)
            }

            startActivity(Intent(requireActivity(), EditMenuActivity::class.java))
        }
        binding.uploadMenu.setOnClickListener {
           val user=mess.getUser()
                    if (isAdded && (user.designation ==DEVELOPER || user.designation == COORDINATOR)) {
                        navigateSafely(R.id.action_mcMainPage_to_uploadMenuFragment)
                    } else {
                        mess.showAlertDialog(
                            "Error",
                            "You are not authorised to upload menu",
                            "Ok",
                            ""
                        ) {}
                    }

        }
        binding.ivBack.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.more.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun navigateSafely(actionId: Int) {
        runCatching {
            if (isAdded && findNavController().currentDestination?.id == R.id.mcMainPage) {
                findNavController().navigate(actionId)
            }
        }
    }

    private fun setValues() {
        val user=mess.getUser()
                binding.tvname.text = user.name
                binding.tvDesignation.text = user.designation
                binding.tvYear.text = "Batch-${user.passingYear}"
                binding.tvEmail.text = user.email
                if (isAdded) {
                    mess.loadImage(user.photoUrl, binding.ivUser)
                }
                val designationIcon = when (user.designation) {
                    "Coordinator" -> R.drawable.coordinator
                    "Developer" -> R.drawable.developer
                    "Senior Member" -> R.drawable.seniormember
                    "Member" -> R.drawable.member
                    else -> R.drawable.volunteer
                }
                binding.ivDesignation.setImageResource(designationIcon)

    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.mc_more, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_review -> {
                    navigateSafely(R.id.action_mcMainPage_to_reviewFragment)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun setTab() {
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle) // Use childFragmentManager
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Polls"
                1 -> "Messages"
                else -> ""
            }
        }.attach()
    }
}
