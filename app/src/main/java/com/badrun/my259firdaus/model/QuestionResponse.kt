package com.badrun.my259firdaus.model

data class QuestionResponse(val code: Int,
                            val message: String,
                            val questions: List<Soal>)
