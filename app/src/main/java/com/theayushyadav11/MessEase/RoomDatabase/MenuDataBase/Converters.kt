package com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theayushyadav11.MessEase.Models.DayMenu
import com.theayushyadav11.MessEase.Models.User

class Converters {

    @TypeConverter
    fun fromUser(user: User): String {
        return Gson().toJson(user)
    }

    @TypeConverter
    fun toUser(userString: String): User {
        return Gson().fromJson(userString, User::class.java)
    }

    @TypeConverter
    fun fromDayMenuList(dayMenu: List<DayMenu>): String {
        return Gson().toJson(dayMenu)
    }

    @TypeConverter
    fun toDayMenuList(dayMenuString: String): List<DayMenu> {
        val listType = object : TypeToken<List<DayMenu>>() {}.type
        return Gson().fromJson(dayMenuString, listType)
    }
}
