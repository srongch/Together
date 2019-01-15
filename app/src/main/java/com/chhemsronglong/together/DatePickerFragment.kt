package com.chhemsronglong.together

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import java.util.*
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_posting.*
import kotlinx.android.synthetic.main.activity_posting.view.*


class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var dateSetFunction : (dateString : String) -> Unit? = {}

    fun onDateSelected (func: (dateString : String) -> Unit){
        dateSetFunction = func
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
      //  val date_input_editor  = activity!!.findViewById(R.id.date_input_editor) as TextInputEditText
      dateSetFunction.invoke("$day-${month + 1}-$year")
    }

}