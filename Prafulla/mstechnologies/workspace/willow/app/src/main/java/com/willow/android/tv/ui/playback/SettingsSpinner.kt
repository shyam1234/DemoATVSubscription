package com.willow.android.tv.ui.playback

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.Spinner


@SuppressLint("AppCompatCustomView")
class SettingsSpinner : Spinner {
    private var spinnerEventsListener: OnSpinnerEventsListener? = null
    private var mOpenInitiated = false

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, mode: Int) : super(
        context,
        attrs,
        defStyleAttr,
        mode
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, mode: Int) : super(context, mode) {}
    constructor(context: Context?) : super(context) {}

    interface OnSpinnerEventsListener {
        fun onSpinnerOpened(spinner: Spinner)
        fun onSpinnerClosed(spinner: Spinner)
    }

    override fun performClick(): Boolean {
        // register that the Spinner was opened so we have a status
        // indicator for the activity(which may lose focus for some other
        // reasons)
        mOpenInitiated = true
        if (spinnerEventsListener != null) {
            spinnerEventsListener?.onSpinnerOpened(this)
        }
        return super.performClick()
    }

    fun setSpinnerEventsListener(onSpinnerEventsListener: OnSpinnerEventsListener) {
        spinnerEventsListener = onSpinnerEventsListener
    }

    /**
     * Propagate the closed Spinner event to the listener from outside.
     */
    fun performClosedEvent() {
        mOpenInitiated = false
        if (spinnerEventsListener != null) {
            spinnerEventsListener?.onSpinnerClosed(this)
        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    fun hasBeenOpened(): Boolean {
        return mOpenInitiated
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasBeenOpened() && hasWindowFocus) {
            performClosedEvent()
        }
    }
}