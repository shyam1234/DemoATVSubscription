package com.willow.android.tv.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.willow.android.tv.common.genericDialogBox.GenericDialogFragment
import com.willow.android.tv.common.genericDialogBox.GenericDialogModel
import com.willow.android.tv.common.genericDialogBox.IGenericDialogClickListener
import com.willow.android.tv.ui.login.LoginActivity

object NavigationUtils {
    fun onAddToFragmentContainer(
        activityContext: AppCompatActivity?,
        container: Int,
        fragment: Fragment,
        addToBackStack: Boolean = false
    ) {
        activityContext?.let {
            val supportFragmentManager: FragmentManager = it.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(container, fragment)
            if (addToBackStack)
                fragmentTransaction.addToBackStack(fragment.javaClass.name)
            if (!it.supportFragmentManager.isStateSaved) {
                fragmentTransaction.commit()
            } else {
                fragmentTransaction.commitAllowingStateLoss()
            }
        }
    }

    fun refreshActivity(activity: FragmentActivity?) {
        activity?.startActivity(Intent(activity, activity.javaClass))
        activity?.finish()
    }

    /**
     * Add new fragment to the fragment container view
     */
    fun onAddToFragmentContainer(
        activityContext: AppCompatActivity?,
        supportFragmentManager: FragmentManager,
        container: Int,
        fragment: Fragment,
        addToBackStack: Boolean
    ) {
        activityContext?.let {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(container, fragment)
            if (addToBackStack)
                fragmentTransaction.addToBackStack(fragment.javaClass.name)
            if (!it.supportFragmentManager.isStateSaved) {
                fragmentTransaction.commit()
            } else {
                fragmentTransaction.commitAllowingStateLoss()
            }
        }
    }

    /**
     * Add new fragment to the fragment container view
     */
    fun onReplaceToFragmentContainer(
        activityContext: AppCompatActivity?,
        container: Int,
        fragment: Fragment,
        addToBackStack: Boolean = false
    ) {
        activityContext?.let {
            val supportFragmentManager: FragmentManager = it.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(container, fragment, fragment.javaClass.name)
            if (addToBackStack)
                fragmentTransaction.addToBackStack(fragment.javaClass.name)
            if (!it.supportFragmentManager.isStateSaved) {
                fragmentTransaction.commit()
            } else {
                fragmentTransaction.commitAllowingStateLoss()
            }
        }
    }

    fun onReplaceToFragmentContainer(
        activityContext: AppCompatActivity?,
        supportFragmentManager: FragmentManager,
        container: Int,
        fragment: Fragment,
        addToBackStack: Boolean = false
    ) {
        activityContext?.let {
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(container, fragment)
            if (addToBackStack)
                fragmentTransaction.addToBackStack(fragment.javaClass.name)
            if (!it.supportFragmentManager.isStateSaved) {
                fragmentTransaction.commit()
            } else {
                fragmentTransaction.commitAllowingStateLoss()
            }
        }
    }

    fun onReplaceToFragmentContainer(
        activityContext: AppCompatActivity?,
        container: FragmentContainerView,
        fragment: Fragment,
        addToBackStack: Boolean = false
    ) {
        activityContext?.let {
            val supportFragmentManager: FragmentManager = it.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(container.id, fragment)
            if (addToBackStack)
                fragmentTransaction.addToBackStack(fragment.javaClass.name)
            if (!it.supportFragmentManager.isStateSaved) {
                fragmentTransaction.commit()
            } else {
                fragmentTransaction.commitAllowingStateLoss()
            }
        }
    }


    /**
     * Navigate to target activity and finish the current activity
     */
    fun navigateTo(
        activityContext: AppCompatActivity?,
        activity: Class<LoginActivity>,
        isCurrentActivityFinish: Boolean = true
    ) {
        activityContext?.let {
            val intent = Intent(it, activity)
            it.startActivity(intent)
            if (isCurrentActivityFinish) {
                it.finish()
            }
        }
    }

    fun navigateTo(
        activityContext: Activity?,
        activity: Class<LoginActivity>,
        isCurrentActivityFinish: Boolean = true
    ) {
        activityContext?.let {
            val intent = Intent(it, activity)
            it.startActivity(intent)
            if (isCurrentActivityFinish) {
                it.finish()
            }
        }
    }

    fun showGenericDialogFragment(
        activity: FragmentActivity?,
        model: GenericDialogModel?,
        listner: IGenericDialogClickListener?
    ) {
        activity?.supportFragmentManager?.let {
            GenericDialogFragment.newInstance(model, listner)
                .show(it, model?.dialogTag)
        }
    }

    fun showDialogFragment(
        activity: FragmentActivity?,
        dialogFragment: DialogFragment,
        tag: String? = dialogFragment.tag
    ) {
        activity?.supportFragmentManager?.let {
            dialogFragment.show(it, tag)
        }
    }

    fun popFragment(activity: FragmentActivity?) {
        activity?.supportFragmentManager?.popBackStack()
    }

    fun removeFragment(
        activity: FragmentActivity?,
        fragment: Fragment
    ) {
        val supportFragmentManager: FragmentManager? = activity?.supportFragmentManager
        supportFragmentManager?.let {
            val fragmentTransaction: FragmentTransaction = it.beginTransaction()
            fragmentTransaction.remove(fragment)
            if (!it.isStateSaved) {
                fragmentTransaction.commit()
            } else {
                fragmentTransaction.commitAllowingStateLoss()
            }
        }
    }
}