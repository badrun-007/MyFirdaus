package com.badrun.my259firdaus.model

data class ResponseCheckPayment (val code : Int,
                                 val message:String,
                                 val payment: Boolean,
                                 val formulir_exists : Boolean,
                                 val porto_exists:Boolean
)