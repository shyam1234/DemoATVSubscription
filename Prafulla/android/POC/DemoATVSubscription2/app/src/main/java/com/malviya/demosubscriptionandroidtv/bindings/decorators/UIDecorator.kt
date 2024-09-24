package com.malviya.demosubscriptionandroidtv.bindings.decorators

import android.widget.Button
import android.widget.TextView

interface UIDecorator {
    fun setColor(textView: Button)
    fun setText(textView: Button, str:String)
}