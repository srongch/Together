package com.chhemsronglong.together.DialogCustom

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.chhemsronglong.together.R
import kotlinx.android.synthetic.main.sample_alert_dialog.view.*

class CustomAlertDialog(context: Context) : BaseDialogHelper() {

    //  dialog view



    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.sample_alert_dialog, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    //  notes edit text
    val messageTextView: TextView by lazy {
        dialogView.findViewById<TextView>(R.id.message_title)
    }

    //  done icon
    private val closeButton: Button by lazy {
        dialogView.close_button
    }

    //  close icon
    private val doneButton: Button by lazy {
        dialogView.done_button
    }

    fun removeButton(){
        closeButton.visibility = View.GONE
    }

    //  closeIconClickListener with listener
    fun closeIconClickListener(func: (() -> Unit)? = null) =
            with(closeButton) {
                setClickListenerToDialogIcon(func)
            }

    //  doneIconClickListener with listener
    fun doneIconClickListener(func: (() -> Unit)? = null) =
            with(doneButton) {
                setClickListenerToDialogIcon(func)
            }

    //  view click listener as extension function
    private fun View.setClickListenerToDialogIcon(func: (() -> Unit)?) =
            setOnClickListener {
                func?.invoke()
                dialog?.dismiss()
            }
}