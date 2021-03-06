package com.geuso.disrupty.settings

import android.content.Context
import android.preference.EditTextPreference
import android.util.AttributeSet
import com.geuso.disrupty.R

/**
 * Custom EditTextPreference that will format the preference summary message with the value.
 * Taken from https://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su/25083261#25083261
 */
class FormattedSummaryEditTextPreference : EditTextPreference {

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)


    override fun getSummary(): CharSequence {
        val summary = super.getSummary().toString()
        return String.format(summary, text)
    }

}