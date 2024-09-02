package com.theayushyadav11.MessEase.ui.NavigationDrawers.viewModelFactories


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDao
import com.theayushyadav11.MessEase.ui.MessCommittee.viewModels.EditCompleteViewModel
import com.theayushyadav11.MessEase.ui.NavigationDrawers.ViewModels.HomeViewModel

class HomeViewModelFactory(private val menuDao: MenuDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(menuDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
