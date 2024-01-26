package com.willow.android.tv.utils


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar


fun Context.toast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG ).show()
}

fun ProgressBar.show(){
    visibility = View.VISIBLE
}

fun ProgressBar.hide(){
    visibility = View.GONE
}

fun View.show(){
    visibility = View.VISIBLE
}

fun View.hide(){
    visibility = View.GONE
}

fun View.snackbar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun Button.setBackgroundSelecter() {
    this.setOnFocusChangeListener { v, hasFocus ->
//        this.background = ContextCompat.getDrawable(
//            context,
//            if (hasFocus) {
//                R.drawable.button_focus_background
//            } else {
//                R.drawable.button_unfocus_background
//            }
//        )
    }
}

fun MaterialButton.changeColorOnFocusChange(
    backgroundColor: Int = Color.TRANSPARENT,
    textColor: Int = Color.BLACK,
    backgroundTintList: ColorStateList? = ColorStateList.valueOf(Color.WHITE),
    textTintList: ColorStateList? = ColorStateList.valueOf(Color.BLACK)
) {
    this.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
        if (hasFocus) {
            this.setBackgroundColor(backgroundColor)
            this.setTextColor(textColor)
            this.iconTint = textTintList
            this.backgroundTintList = backgroundTintList
            this.setTextColor(textTintList)
        } else {
            this.setBackgroundColor(Color.parseColor("#4A5050"))
            this.iconTint = ColorStateList.valueOf(Color.parseColor("#BDC2C2"))
            this.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4A5050"))
            this.setTextColor(Color.parseColor("#BDC2C2"))
        }
    }
}

fun RadioButton.changeColorOnFocusChange(
    backgroundColor: Int = Color.TRANSPARENT,
    textColor: Int = Color.BLACK,
    backgroundTintList: ColorStateList? = ColorStateList.valueOf(Color.WHITE),
    textTintList: ColorStateList? = ColorStateList.valueOf(Color.BLACK)
) {
    this.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
        if (hasFocus) {
            this.setBackgroundColor(backgroundColor)
            this.setTextColor(textColor)
            this.backgroundTintList = backgroundTintList
            this.setTextColor(textTintList)
        } else {
            this.setBackgroundColor(Color.GRAY)
            this.setTextColor(this.textColors.defaultColor)
            this.backgroundTintList = ColorStateList.valueOf(Color.DKGRAY)
            this.setTextColor(Color.WHITE)
        }
    }
}