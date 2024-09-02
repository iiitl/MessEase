package com.theayushyadav11.MessEase.Models

import java.util.Date

class OptionSelected(
    val selected: String = "",
    val time: String = "",
    val date: String = "",
    val user:User=User(),
    val comp: Date = Date()
)

