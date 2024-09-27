package com.theayushyadav11.MessEase.Models.supabase

import kotlinx.serialization.Serializable

@Serializable
data class Options(
    val opt_id: String="",
    val poll_id: String="",
    val opt_text: String="",
)
