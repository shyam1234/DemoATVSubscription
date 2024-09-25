package com.malviya.demosubscriptionandroidtv.bindings.decorators

import android.annotation.SuppressLint
import android.widget.Button
import androidx.core.content.ContextCompat
import com.malviya.demosubscriptionandroidtv.R


class GreenUIDecorator : UIDecorator {
    override fun setColor(button: Button) {
        button.setTextColor(ContextCompat.getColor(button.context, R.color.green))
    }

    override fun setText(button: Button, str: String) {
        button.text = str
    }
}