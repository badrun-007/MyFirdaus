package com.badrun.my259firdaus.model

data class ResponsePeminjaman(val code: Int,
                              val message: String,
                              val data: List<PeminjamanData>)
