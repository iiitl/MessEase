package com.theayushyadav11.MessEase.ui.more

import android.content.Context
import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Mess

class ReviewViewModel:ViewModel() {


    fun getMainMenu(context: Context, onResult:(Menu)->Unit)
    {
        Mess(context).getMainMenu {
            onResult(it)
        }
    }
}