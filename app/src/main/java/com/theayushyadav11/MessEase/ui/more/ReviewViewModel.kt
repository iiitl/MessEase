package com.theayushyadav11.MessEase.ui.more

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase

class ReviewViewModel:ViewModel() {


    fun getMainMenu(onResult:(Menu)->Unit)
    {
        fireBase.getMainMenu {
            onResult(it)
        }
    }
}