package com.theayushyadav11.MessEase.ui.MessCommittee.viewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDao
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.EditCompleteViewModel

class EditCompleteViewModelFactory(private val menuDao: MenuDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditCompleteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditCompleteViewModel(menuDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
