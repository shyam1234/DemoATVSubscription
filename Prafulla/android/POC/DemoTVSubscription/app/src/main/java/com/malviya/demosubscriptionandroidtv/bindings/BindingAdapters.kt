package com.malviya.demosubscriptionandroidtv.bindings

import android.widget.Button
import com.malviya.demosubscriptionandroidtv.bindings.decorators.UIDecorator

object BindingAdapters {
    // TextView Binding Adapter for applying decorator to text
    @JvmStatic
    @androidx.databinding.BindingAdapter("decorator", "applySubscription")
    fun applySubscriptionDecorator(btn: Button, decorator: UIDecorator, str: String) {
        decorator.setColor(btn)
        decorator.setText(btn, str)
    }
}