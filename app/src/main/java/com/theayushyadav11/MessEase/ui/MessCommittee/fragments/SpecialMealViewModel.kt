package com.theayushyadav11.MessEase.ui.MessCommittee.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theayushyadav11.MessEase.Models.SpecialMeal
import com.theayushyadav11.MessEase.notifications.PushNotifications
import com.theayushyadav11.MessEase.utils.Constants.Companion.SPECIAL_MEAL
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpecialMealViewModel : ViewModel() {
    val day = MutableLiveData<Int>(0)
    val month = MutableLiveData<Int>(0)
    val year = MutableLiveData<Int>(0)
    val mealIndex = MutableLiveData<Int>(0)
    val mealName = MutableLiveData<String>("")

    fun uploadSpecialMeal(onResult: () -> Unit) = viewModelScope.launch(Dispatchers.IO)
    {
        val specialMeal = SpecialMeal(
            System.currentTimeMillis(),
            day.value!!,
            month.value!!,
            year.value!!,
            mealIndex.value!!,
            mealName.value!!
        )
        firestoreReference.collection(SPECIAL_MEAL).add(specialMeal).addOnCompleteListener {
            onResult()
        }
    }


}