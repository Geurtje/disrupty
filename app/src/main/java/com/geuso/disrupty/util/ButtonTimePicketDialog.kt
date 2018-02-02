package com.geuso.disrupty.util

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.TimePicker
import java.util.*

/**
 * Customization of TimePickerDialog that can update the label of a button with the selected time.
 *
 * TODO Use proper localization to format the text, use Calendar object to extract current time
 * instead of the button text
 */
class ButtonTimePicketDialog : TimePickerDialog {

    companion object {
        private val TAG = ButtonTimePicketDialog::class.qualifiedName
    }

    private var button : Button

    constructor(context: Context, is24HourView: Boolean, button: Button) :
            super(context, null, 0, 0, is24HourView) {
        this.button = button

        val hourMinute = extractTimeFromCurrentButtonText()

        val hourOfDay = hourMinute.first
        val minuteOfHour = hourMinute.second
        super.updateTime(hourOfDay, minuteOfHour)
    }

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        super.onTimeChanged(view, hourOfDay, minute)

        val minuteStr = String.format("%02d", minute)
        button.text = "${hourOfDay}:${minuteStr}"
    }

    private fun extractTimeFromCurrentButtonText() : Pair<Int, Int> {
        val textParts : List<String> = button.text.split(":")

        if (textParts.size >= 2) {
            try {
                val hour = Integer.valueOf(textParts[0])
                val minute = Integer.valueOf(textParts[1])
                return Pair(hour, minute)
            }
            catch (e: NumberFormatException){
                Log.i(TAG, "Unable to extract time from button text '${button.text}'. Falling back to current time.")
            }
        }

        val calendar = Calendar.getInstance()
        return Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

}