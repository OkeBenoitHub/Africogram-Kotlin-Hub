package com.africogram.www.utils

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.africogram.www.R

/**
 * Dialog Util :: contain every recurring task dealing with Android Dialog
 */
class DialogUtil: DialogFragment() {
    /**
     * Dialog properties
     */
    private var dialogTitle: String? = null
    private var dialogContentTextMessage: String? = null
    private var dialogResLayoutInt: Int? = null
    private var isDialogCancelTapOutSide: Boolean = false
    private var dialogPositiveBtnText: String? = null
    private var dialogNegativeBtnText: String? = null

    private lateinit var listener: ShowAlertDialogCallbackListener

    private lateinit var builder: AlertDialog.Builder

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface ShowAlertDialogCallbackListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    /**
     * Set dialog properties
     * @param title :: dialog title
     * @param contentTextMessage :: content text message
     * @param resLayoutInt :: res layout int eg: R.layout.dialog
     * @param isCancelTapOutside :: should the dialog get dismissed by tapping outside of it.
     */
    fun setDialogProperties(
        title: String,
        contentTextMessage: String?,
        resLayoutInt: Int?,
        isCancelTapOutside: Boolean,
        positiveBtnText: String?,
        negativeBtnText: String?,
        ShowAlertDialogCallbackListener: ShowAlertDialogCallbackListener
    ) {
        dialogTitle = title
        dialogContentTextMessage = contentTextMessage
        dialogResLayoutInt = resLayoutInt
        isDialogCancelTapOutSide = isCancelTapOutside
        dialogPositiveBtnText = positiveBtnText
        dialogNegativeBtnText = negativeBtnText
        listener = ShowAlertDialogCallbackListener
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Build an AlertDialog
            builder = AlertDialog.Builder(
                it,
                R.style.MyAlertDialogStyle
            )
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            // Set a title for alert dialog
            builder.setTitle(dialogTitle)
            // set content view or message
            if (dialogContentTextMessage == null) {
                dialogResLayoutInt?.let {
                    val view = inflater.inflate(dialogResLayoutInt!!, null)
                    builder.setView(view)
                }
            } else {
                builder.setMessage(dialogContentTextMessage)
            }
            builder.setCancelable(isDialogCancelTapOutSide)
            // Set the alert dialog yes button click listener
            builder.setPositiveButton(dialogPositiveBtnText) { _, _ ->
                // Do something when user clicked the Yes button
                listener.onDialogPositiveClick(this)
            }

            // Set the alert dialog no button click listener
            builder.setNegativeButton(dialogNegativeBtnText) { _, _ ->
                // Do something when No button clicked
                listener.onDialogNegativeClick(this)
            }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}