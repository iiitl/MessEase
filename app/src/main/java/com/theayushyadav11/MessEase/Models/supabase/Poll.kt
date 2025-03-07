package com.theayushyadav11.MessEase.Models.supabase


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
