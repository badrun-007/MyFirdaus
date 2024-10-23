package com.badrun.my259firdaus.model

data class ResponseTransactionDetail(
    val statusCode: String,
    val transactionId: String,
    val grossAmount: String,
    val currency: String,
    val orderId: String,
    val paymentType: String,
    val signatureKey: String,
    val transactionStatus: String,
    val fraudStatus: String,
    val statusMessage: String,
    val merchantId: String,
    val vaNumbers: List<VirtualAccount>,
    val paymentAmounts: List<PaymentAmount>,
    val transactionTime: String,
    val expiryTime: String
)

data class VirtualAccount(
    val bank: String,
    val vaNumber: String
)

data class PaymentAmount(
    val amount: String,
    val paidAt: String
)


