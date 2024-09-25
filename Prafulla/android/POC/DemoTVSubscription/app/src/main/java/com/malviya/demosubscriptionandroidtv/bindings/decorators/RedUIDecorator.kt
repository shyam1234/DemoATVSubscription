package com.malviya.demosubscriptionandroidtv.bindings.decorators

import android.annotation.SuppressLint
import android.widget.Button
import androidx.core.content.ContextCompat
import com.malviya.demosubscriptionandroidtv.R

class RedUIDecorator : UIDecorator {
    override fun setColor(button: Button) {
        button.setTextColor(ContextCompat.getColor(button.context, R.color.red))
    }
    override fun setText(Button: Button, str : String) {
        Button.text = str
    }
}