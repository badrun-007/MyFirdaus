package com.badrun.my259firdaus.model

data class MidtransRequest(val transaction_type:String, val transaction_details: TransactionDetails,
                           val customer_details: CustomerDetails) {
    data class TransactionDetails(
        val order_id: String,
        val gross_amount: Int
    )

    data class CustomerDetails(
        val id_pendaftaran : Int,
        val name_santri: String,
        val gender: String,
        val ayah: String,
        val ibu: String,
        val token_hp : String
    )
}

