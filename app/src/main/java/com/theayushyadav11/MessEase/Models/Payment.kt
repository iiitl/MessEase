package com.theayushyadav11.MessEase.Models

data class Payment(

    val id:String="",
    val uid:String="",
    val amount:Double=0.0,
    val purpose:String="",
    val timeStamp:Long=0,
    val name:String="",
    val email:String="",
    val status: PaymentStatus=PaymentStatus.PENDING



)
enum class PaymentStatus{
    PENDING,
    SUCCESS,
    FAILED
}