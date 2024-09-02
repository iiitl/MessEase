package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDao
import com.theayushyadav11.MessEase.Models.Menu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditMenuViewModel(private val menuDao: MenuDao) : ViewModel() {


    private var _currentMenu: Menu? = null


    val currentMenu: Menu?
        get() = _currentMenu


    fun setCurrentMenu(menu: Menu) {
        _currentMenu = menu
    }


    fun getMenuCurrentMenu(onResult: (Menu) -> Unit) {


            viewModelScope.launch(Dispatchers.IO) {
                val menu = menuDao.getMenu()
                _currentMenu = menu
                onResult(menu)
            }

    }


    fun setEditedMenu(menu: Menu) {
        val nMenu = Menu(id = 1, menu = menu.menu, creator = menu.creator)
        viewModelScope.launch(Dispatchers.IO) {
            menuDao.addMenu(nMenu)
            _currentMenu = nMenu
        }
    }
}
