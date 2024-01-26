package com.willow.android.tv.utils.extension


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.leanback.tab.LeanbackTabLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.config.GlobalTVConfig
import java.security.MessageDigest


inline fun <reified T : Any> Activity.launchActivityForResult (
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}


inline fun <reified T : Any> Activity.launchActivity (
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {})
{
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
    finish()
}


inline fun <reified T : Any> Context.launchActivity (
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {})
{
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)


inline fun <reified T : Any> Context.startActivityWithData(data: Parcelable) {
    val intent = Intent(this, T::class.java).apply {
        putExtra("data", data)
    }
    startActivity(intent)
}

inline fun <reified T : Any> Context.startActivityWithOutData(finishActivity: Boolean?=false) {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
    if(finishActivity == true)
        if (this is Activity) {
            finish()
        }
}


inline fun <reified T : Any> Context.startActivityWithData(data1: Parcelable, data2: Parcelable?, screenName : String? = GlobalTVConfig.currentPage ) {
    val intent = Intent(this, T::class.java).apply {
        putExtra("data1", data1)
        putExtra("data2", data2)
        putExtra("screen_name", screenName)
    }
    startActivity(intent)
}

inline fun <reified T : Any> Context.startActivityWithData(
    activityType: GlobalConstants.ActivityType,
    data: Any?
) {
    val intent = Intent(this, T::class.java)
    startActivity(getActivityExtra(intent, activityType, data))
}

fun getActivityExtra(
    intent: Intent,
    activityType: GlobalConstants.ActivityType,
    data: Any?
): Intent {
    return intent.apply {
        if (activityType == GlobalConstants.ActivityType.DETAILS_PAGE) {
            putExtra(GlobalConstants.Keys.URL, data as String?)
        }
    }

}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

fun TextView.text(text: String?) {
    if (!TextUtils.isEmpty(text)) {
        this.visibility = View.VISIBLE
        this.text = text
    } else {
        this.visibility = View.GONE
    }
}

fun TextView.text(text: Spanned?) {
    if (!TextUtils.isEmpty(text)) {
        this.visibility = View.VISIBLE
        this.text = text
    } else {
        this.visibility = View.GONE
    }
}


fun Activity.showAlertDialog(title: String, message: String) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        .setCancelable(false)
        .show()
}
fun ImageView.loadImageUrl(image_url: String?, defultImg: Int) {
    Glide.with(this.context)
        .applyDefaultRequestOptions(
            RequestOptions()
                .placeholder(defultImg)
                .error(defultImg)
        )
        .load(image_url)
        .into(this)
}

fun LeanbackTabLayout.makeSpaceBetweenTab() {
    for (i in 0 until tabCount) {
        val tab = (getChildAt(0) as ViewGroup).getChildAt(i)
        val p = tab.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(0, 0, 20, 0)
        tab.requestLayout()
    }
}


fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

fun NavController.navigateSafe(@IdRes resId: Int, args: Bundle? = null) {
    val destinationId = currentDestination?.getAction(resId)?.destinationId
    currentDestination?.let { node ->
        val currentNode = when (node) {
            is NavGraph -> node
            else -> node.parent
        }
        destinationId?.let {
            if (it != 0) {
                currentNode?.findNode(it)?.let { navigate(resId, args) }
            }
        }
    }}


inline fun <reified T : Any> T.toJsonHashUsingGson(): String {
    val gson = Gson()
    val jsonString = gson.toJson(this)
    val bytes = MessageDigest.getInstance("SHA-256").digest(jsonString.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}


