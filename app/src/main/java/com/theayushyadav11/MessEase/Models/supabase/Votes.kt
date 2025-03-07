package com.theayushyadav11.MessEase.Models.supabase


data class Votes(
    val id: String="",
    val poll_id: String="",
    val uid: String="",
    val opt_id: String="",
    val voted_at: String="",
)
