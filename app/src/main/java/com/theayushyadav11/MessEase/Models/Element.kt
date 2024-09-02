package com.theayushyadav11.MessEase.Models

data class Element(
    val type: Int=1,
    val poll: Poll= Poll(),
    val msg: Msg= Msg(),
    val particulars: Particulars= Particulars()
)
