package com.badrun.my259firdaus.model

data class Payment(
    val orderId: String,
    val status: String,
    val grossAmount: String,
    val paymentType: String,
    val timeOrder: String,
    val expiryTime : String,
    val snapToken : String
)