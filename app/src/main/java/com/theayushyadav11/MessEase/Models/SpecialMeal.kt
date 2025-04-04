package com.theayushyadav11.MessEase.Models

data class SpecialMeal(
    val timestamp:Long=System.currentTimeMillis(),
    val day:Int = 0,
    val month:Int=0,
    val year:Int=0,
    val mealIndex:Int=0,
    val food:String="",
)
