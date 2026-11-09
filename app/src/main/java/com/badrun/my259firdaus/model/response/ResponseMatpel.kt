package com.badrun.my259firdaus.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ResponseMatpel(
    val code: Int,
    val message:String,
    val data: List<MatpelGuru>
){
    @Parcelize
    data class MatpelGuru(
        val id: Int,
        val guru_id: Int,
        val matpel_id: Int,
        val kelas_id: Int,
        val matpel: Matpel,
        val kelas: Kelas
    ): Parcelable

    @Parcelize
    data class Matpel(
        val id: Int,
        val name: String
    ): Parcelable

    @Parcelize
    data class Kelas(
        val id: Int,
        val name: String
    ): Parcelable
}
