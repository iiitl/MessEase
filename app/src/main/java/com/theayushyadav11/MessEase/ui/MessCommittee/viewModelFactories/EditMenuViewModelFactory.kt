package com.theayushyadav11.MessEase.ui.MessCommittee.viewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDao
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.EditMenuViewModel

class EditMenuViewModelFactory(private val menuDao: MenuDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditMenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditMenuViewModel(menuDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
