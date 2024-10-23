package com.badrun.my259firdaus.model

data class Soal(val id: Int,
                val question_text: String,
                val passage: String?,
                val image_url: String?,
                val correct_option: String,
                val options: List<Option>){

    data class Option(
        val id: Int,
        val label: String,
        val text: String,
        val image_url: String?
    )
}


