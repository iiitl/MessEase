package com.theayushyadav11.MessEase.Models

import java.util.Date

data class Msg(
    val uid: String = "",
    val creater:User=User(),
    val time: String = "",
    val date: String = "",
    val comp: Date = Date(),
    val title: String = "",
    val body: String = "",
    val photos: List<String> = emptyList(),
    val target: String = "",
)
