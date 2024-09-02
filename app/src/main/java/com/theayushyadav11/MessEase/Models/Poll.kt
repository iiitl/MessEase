package com.theayushyadav11.MessEase.Models

import java.util.Date

data class Poll(
    val id: String = "",
    val creater: User = User(),
    val question: String = "",
    val comp: Date = Date(),
    val date: String = "",
    val time: String = "",
    var totalVotes: Int = 0,
    var isMultiple: Boolean = false,
    val options: MutableList<String> = mutableListOf(),
    val target: String = ""

)
