package com.theayushyadav11.MessEase.Models

import java.util.Date

data class AprMenu(
    val key: String="",
    val note: String="",
    val url: String="",
    val date: Date =Date(),
    val menu: Menu=Menu(),
    val displayDate: String="",
    val comp:String=""
)
