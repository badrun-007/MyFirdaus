package com.badrun.my259firdaus.helper

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.badrun.my259firdaus.R
import java.util.Calendar

class MonthYearPickerDialogFragment : DialogFragment() {

    private var onDateSetListener: ((year: Int, month: Int) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_month_year_picker, null)

        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.month_picker)
        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        monthPicker.value = currentMonth
        monthPicker.displayedValues = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.year_picker)
        yearPicker.minValue = 1900
        yearPicker.maxValue = currentYear + 100
        yearPicker.value = currentYear

        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                onDateSetListener?.invoke(yearPicker.value, monthPicker.value)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    fun setOnDateSetListener(listener: (year: Int, month: Int) -> Unit) {
        onDateSetListener = listener
    }

    companion object {
        fun getInstance(month: Int, year: Int): MonthYearPickerDialogFragment {
            val fragment = MonthYearPickerDialogFragment()
            val args = Bundle().apply {
                putInt("month", month)
                putInt("year", year)
            }
            fragment.arguments = args
            return fragment
        }
    }
}