package com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.theayushyadav11.MessEase.Models.Menu

@Dao
interface MenuDao {

    @Upsert
    suspend fun addMenu(menu: Menu)

    @Query("SELECT *FROM menu Where id =0")
    suspend fun getMenu(): Menu

    @Query("SELECT *FROM menu Where id =1")
    suspend fun getEditedMenu(): Menu

    @Query("SELECT *FROM menu Where id =2")
    fun getShowMenu(): Menu

    @Query("SELECT *FROM menu Where id =:id")
    fun getMenuFromId(id:Int): Menu

}