package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.SpecialMeal
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESCENDING_ORDER
import com.theayushyadav11.MessEase.utils.Constants.Companion.SPECIAL_MEAL
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference

class SpecialMealHistoryViewModel : ViewModel() {
    // TODO: Implement the ViewModel


    fun getHistory(onResult: (List<SpecialMeal>) -> Unit) {
        firestoreReference.collection(SPECIAL_MEAL)
            .orderBy("timestamp",DESCENDING_ORDER)
            .addSnapshotListener { snapshot, e ->
            if (e != null) {
                onResult(emptyList())
                return@addSnapshotListener
            }
            val meals = snapshot?.toObjects(SpecialMeal::class.java)
            onResult(meals ?: emptyList())
        }
    }
}