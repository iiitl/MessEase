package com.theayushyadav11.MessEase.Models

import java.util.Date

data class Comment(
    val id: String="",
    val creator:User=User(),
    val comment:String="",
    val comp:Date=Date(),
    val time:String="",
    val date:String="",
)
