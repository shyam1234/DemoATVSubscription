package com.willow.android.tv.common.base

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.willow.android.R
import com.willow.android.tv.common.navmenu.NavigationMenuCallback
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.ErrorUtils.hideErrorPage
import com.willow.android.tv.utils.ErrorUtils.showErrorPage
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show

open class BaseFragment : Fragment() {


    protected var navigationMenuCallback: NavigationMenuCallback? = null
    fun setNavigationCallback(callback: NavigationMenuCallback?) {
        this.navigationMenuCallback = callback
    }

    fun getNavigationCallback():NavigationMenuCallback?{
        return navigationMenuCallback
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideError(view)
    }

    fun showError(view: View?, error: ErrorType, errorMessage: String? = null, errorDetail: String? = null, backBtnListener: View.OnClickListener? = null, btnText:String? = null) {
        view?.let { showErrorPage(it, error, errorMessage, errorDetail, backBtnListener,btnText) }
    }

    fun hideError(view: View) {
        hideErrorPage(view)
    }

    open fun focusItem() {
        //Nothing
    }

    open fun focusParent(){

    }

    fun showLoader(view: View?) {
        view?.let { loader ->
            loader.show()
            val progress = loader.findViewById<ImageView>(R.id.progressBar)
            progress?.let {
                val imageViewAnimator =
                    ObjectAnimator.ofFloat(
                        it,
                        View.ROTATION,
                        359f
                    )
                imageViewAnimator.repeatCount = Animation.INFINITE
                imageViewAnimator.duration = 1000
                imageViewAnimator.start()
            }

        }
    }
    fun hideLoader(view: View?) {
        view?.let { parentView ->
            parentView.hide()
        }
    }

}