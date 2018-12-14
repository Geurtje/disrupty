package com.geuso.disrupty.util

import android.app.TimePickerDialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker

/**
 * Customization of TimePickerDialog that can update the label of a button with the selected time.
 *
 * TODO Use proper localization to format the text, use Calendar object to extract current time
 * instead of the button text
 */
class EditTextTimePicketDialog : TimePickerDialog {

    companion object {
        private val TAG = EditTextTimePicketDialog::class.qualifiedName
    }

    private var editText : EditText

    constructor(context: Context, is24HourView: Boolean, editText: EditText) :
            super(context, null, 0, 0, is24HourView) {
        this.editText = editText

        val hourMinute = extractHourAndMinuteFromText(editText.text)

        val hourOfDay = hourMinute.first
        val minuteOfHour = hourMinute.second
        super.updateTime(hourOfDay, minuteOfHour)
    }

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        super.onTimeChanged(view, hourOfDay, minute)
        editText.setText(formatTime(hourOfDay, minute))
    }


}