package com.badrun.my259firdaus.model

data class PerpusMidtrans(val transaction_type:String, val transaction_details: TransactionDetails,
                          val customer_details: CustomerDetails){

    data class TransactionDetails(
        val order_id: String,
        val gross_amount: Int
    )

    data class CustomerDetails(
        val id_user : Int,
        val role : Int,
        val name_user: String,
        val gender: String,
        val token_hp : String
    )
}


