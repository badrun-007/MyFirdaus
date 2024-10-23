package com.badrun.my259firdaus.helper

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

class numberFormat(private val editText: EditText) : TextWatcher {
    private var current = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Tidak perlu implementasi khusus di sini
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Tidak perlu implementasi khusus di sini
    }

    override fun afterTextChanged(editable: Editable?) {
        editText.removeTextChangedListener(this)

        try {
            var originalString = editable.toString()

            if (originalString != current) {
                val cleanString = originalString.replace("[,.\\s]".toRegex(), "")
                val parsed = cleanString.toDouble()
                val formatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(parsed)

                current = formatted
                editText.setText(formatted)
                editText.setSelection(formatted.length)
            }
        } catch (nfe: NumberFormatException) {
            nfe.printStackTrace()
        } finally {
            editText.addTextChangedListener(this)
        }
    }
}