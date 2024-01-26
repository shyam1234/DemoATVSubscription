package com.willow.android.tv.utils

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.willow.android.R
import com.willow.android.mobile.configs.MessageConfig

object ErrorUtils {

    fun showErrorPage(view: View, error: ErrorType, errorMessage: String? = null, errorDetail: String?, backBtnListener: View.OnClickListener?,btnText:String?) {
        val parentView = view.findViewById<LinearLayout>(R.id.ll_error)
        parentView?.let { parent ->
            parent.visibility = View.VISIBLE
            showErrorTextAndImage(view, error, errorMessage, errorDetail,backBtnListener,btnText)
        }
    }

    fun hideErrorPage(view: View) {
        val parentView = view.findViewById<LinearLayout>(R.id.ll_error)
        parentView?.let { parent ->
            parent.visibility = View.GONE
        }
    }

    private fun showErrorTextAndImage(view: View, error: ErrorType, errorMessage: String? = null, errorDetail: String? = null, backBtnListener: View.OnClickListener? = null, btnText:String? = null) {
        val errorText = view.findViewById<TextView>(R.id.error_title)
        val errorDetailText = view.findViewById<TextView>(R.id.error_detail)
        val errorImage = view.findViewById<ImageView>(R.id.error_image)
        val back = view.findViewById<AppCompatButton>(R.id.backToHomeButton)
        if (btnText.isNullOrEmpty()) {
            back.visibility = View.GONE
        } else {
            back.visibility = View.VISIBLE
            back.requestFocus()
            backBtnListener?.let {
                back.text = btnText
                back.setOnClickListener(it)
            }
        }
        errorText?.let { textView ->
            errorImage?.let { imageView ->
                when (error) {
                    ErrorType.NO_VIDEO_FOUND -> {
                        textView.text = MessageConfig.videoNotFound
                        imageView.setImageResource(R.drawable.no_video_error)
                    }

                    ErrorType.NO_MATCH_FOUND -> {
                        textView.text = MessageConfig.noMatchToday
                        imageView.setImageResource(R.drawable.no_match_error)
                    }

                    ErrorType.NO_DATA_FOUND -> {
                        textView.text = MessageConfig.noDataFound
                        imageView.setImageResource(R.drawable.no_page_error)
                    }

                    else -> {
                        if (!errorMessage.isNullOrEmpty()) {
                            textView.text = errorMessage
                            errorDetailText?.let {
                                it.text = errorDetail ?: ""
                            }
                        } else {
                            textView.text = MessageConfig.noDataFound
                            imageView.setImageResource(R.drawable.no_video_error)
                        }
                    }
                }
            }
        }
    }
}