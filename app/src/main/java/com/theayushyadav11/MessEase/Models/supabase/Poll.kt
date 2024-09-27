package com.theayushyadav11.MessEase.Models.supabase

import com.theayushyadav11.MessEase.Models.User
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Poll(
    val id: String = "",
    val creater: String = "",
    val question: String = "",
    val comp: String = "",
    val date: String = "",
    val time: String = "",
    var isMultiple: Boolean = false,
    val target: String = ""


)
