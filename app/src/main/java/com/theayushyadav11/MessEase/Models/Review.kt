package com.theayushyadav11.MessEase.Models

import java.util.Date

class Review(
    val id:String="",
    val creater:User= User(),
    val comp:Date= Date(),
    val dateTime:String="",
    val rating:Float=0.0f,
    val isSolved:Boolean= false,
    val photos:List<String> = listOf(),
    val food:String="",
    val day:String="",
    val foodtype:String="",
    val review:String="",
)