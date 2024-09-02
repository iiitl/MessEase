package com.theayushyadav11.MessEase.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Menu(
    @PrimaryKey
    val id: Int=0,
    val comp:String=" ",
    val creator:User=User(),
    val menu:List<DayMenu> = listOf(),


)
