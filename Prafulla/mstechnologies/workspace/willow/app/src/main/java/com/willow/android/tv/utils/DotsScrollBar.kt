package com.willow.android.tv.utils
import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.setPadding
import com.willow.android.R
import timber.log.Timber



class DotsScrollBar {
    companion object {
        fun createDotScrollBar(
            context: Context?,
            main_holder: LinearLayout,
            selectedPage: Int,
            count: Int
        ) {
            context?.let { context ->
                for (i in 0 until count) {
                    var dot: ImageView? = null
                    dot = ImageView(context)
                    val vp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    dot.layoutParams = vp
                    context?.resources?.getDimensionPixelSize(R.dimen.indicator_gap)
                        ?.let { dot.setPadding(it) }
                    if (i == selectedPage) {
                        try {
                            dot.setImageResource(R.drawable.indicator_selected)
                        } catch (e: Exception) {
                            Timber.d("inside DotsScrollBar.java", "could not locate identifier")
                        }
                    } else {
                        dot.setImageResource(R.drawable.indicator_unselected)
                    }
                    main_holder.addView(dot)
                }
                main_holder.invalidate()
            }
        }
    }
}