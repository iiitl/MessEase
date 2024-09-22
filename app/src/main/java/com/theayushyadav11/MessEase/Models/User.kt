package com.theayushyadav11.MessEase.Models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String="",
    val name: String="",
    val token:String="",
    var member: Boolean = false,
    val photoUrl: String="",
    val email: String="",
    val designation: String="",
    val batch:String="",
    val passingYear:String="",
    val gender: String="",
)
