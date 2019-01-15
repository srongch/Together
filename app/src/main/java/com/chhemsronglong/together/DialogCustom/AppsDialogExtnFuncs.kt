package com.chhemsronglong.together.DialogCustom

import android.app.Activity
import android.app.AlertDialog
import android.support.v4.app.Fragment

/*
 * Notes Dialog
 */
inline fun Activity.showNotesAlertDialog(func: NotesDialogHelper.() -> Unit): AlertDialog =
        NotesDialogHelper(this).apply {
            func()
        }.create()

inline fun Fragment.showNotesAlertDialog(func: NotesDialogHelper.() -> Unit): AlertDialog =
        NotesDialogHelper(this.context!!).apply {
            func()
        }.create()

inline fun Activity.showAlertDialog(func: CustomAlertDialog.() -> Unit): AlertDialog =
        CustomAlertDialog(this).apply {
            func()
        }.create()

inline fun Fragment.showAlertDialog(func: CustomAlertDialog.() -> Unit): AlertDialog =
        CustomAlertDialog(this.context!! ).apply {
            func()
        }.create()

//inline fun Activity.showAlertDialog(func: CustomAlertDialog.() -> Unit): CustomAlertDialog =
//        CustomAlertDialog(this).apply {
//            func()
//        }.create()
//
//inline fun Fragment.showAlertDialog(func: CustomAlertDialog.() -> Unit): CustomAlertDialog =
//        CustomAlertDialog(this.context!!).apply {
//            func()
//        }.create()
/*
 * TimeChooser Dialog
 */
//inline fun Activity.showTimeChooserAlertDialog(func: TimeChooserDialogHelper.() -> Unit): CustomAlertDialog =
//        TimeChooserDialogHelper(this).apply {
//            func()
//        }.create()
//
//inline fun Fragment.showTimeChooserAlertDialog(func: TimeChooserDialogHelper.() -> Unit): CustomAlertDialog =
//        TimeChooserDialogHelper(this.context!!).apply {
//            func()
//        }.create()