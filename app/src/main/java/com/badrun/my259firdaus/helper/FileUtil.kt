package com.badrun.my259firdaus.helper

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File

object FileUtil {
    fun getPath(context: Context, uri: Uri): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(filePathColumn[0])
                it.getString(columnIndex)
            } else null
        }
    }
}
