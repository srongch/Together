package com.chhemsronglong.together.DialogCustom

import android.app.AlertDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.chhemsronglong.together.R
import kotlinx.android.synthetic.main.notes_dialog.view.*

class NotesDialogHelper(context: Context) : BaseDialogHelper() {

    //  dialog view



    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.notes_dialog, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    //  notes edit text
//    val eText: TextInputEditText by lazy {
//        dialogView.findViewById<TextInputEditText>(R.id.notes_etxt_view)
//    }

    //  done icon
    private val closeButton: Button by lazy {
        dialogView.close_button
    }

    //  close icon
    private val doneButton: Button by lazy {
        dialogView.done_button
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