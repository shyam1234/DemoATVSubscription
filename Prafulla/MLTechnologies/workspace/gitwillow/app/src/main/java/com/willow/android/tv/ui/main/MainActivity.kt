package com.willow.android.tv.ui.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.willow.android.databinding.ActivityMainBinding
import com.willow.android.tv.common.base.BaseActivity
import com.willow.android.tv.data.workers.PollerAPIWorker
import com.willow.android.tv.ui.explorepage.ExploreFragment
import com.willow.android.tv.ui.main.viewmodel.MainViewModel
import com.willow.android.tv.ui.matchcenterpage.MatchCenterFragment
import com.willow.android.tv.utils.CheckConnection
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.NavigationUtils
import com.willow.android.tv.utils.PrefRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * It is main class which uses for navigating the root page i.e Introduction page, Home page etc
 */
class MainActivity : BaseActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: MainViewModel
    private lateinit var prefRepository: PrefRepository

    private var doubleBackToExitPressedOnce = false

    private val mainFragment = MainFragment()

    private val checkConnection by lazy { CheckConnection(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        NavigationUtils.onAddToFragmentContainer(
            this, mBinding.fragmentContainerViewHolder.id,
            mainFragment, false
        )
        prefRepository = PrefRepository(applicationContext)

        initViewModel()

        checkConnection.observe(this@MainActivity) {
            Timber.d("MainActivity Connection Check:: " + it)

            if (!it) {
                showError(mBinding.root, ErrorType.NONE, "NO INTERNET", backBtnListener = {onBackPressed()}, btnText = "Exit")
            } else {
                hideError(mBinding.root)
            }
        }

        mViewModel.refreshPageForSubscription.observe(this@MainActivity) {
            val newFragment = MainFragment()
            supportFragmentManager.beginTransaction()
                .replace(com.willow.android.R.id.fragment_container_view_holder, newFragment)
                .commit()
        }

//        startPeriodicApiRequest()

        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.addOnBackStackChangedListener {
            val currentFragment: Fragment? =
                fragmentManager.findFragmentById(com.willow.android.R.id.fl_main_holder)
            // If any fragment needs to not have left menu, we need to edit following condition by adding fragment to be checked.
            if (currentFragment is MatchCenterFragment) {
                val onNowFragmentView: View? =
                    findViewById(com.willow.android.R.id.fl_main_nav_menu)
                onNowFragmentView?.visibility = View.GONE
            } else {
                val onNowFragmentView: View? =
                    findViewById(com.willow.android.R.id.fl_main_nav_menu)
                onNowFragmentView?.visibility = View.VISIBLE
            }
        }
    }

    fun setToHomePage() {
        mainFragment.setToHomePage()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    fun startPeriodicApiRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val myWorker = PeriodicWorkRequest.Builder(
            PollerAPIWorker::class.java,
            15,
            TimeUnit.MINUTES,
            1,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(getApplication())
            .enqueueUniquePeriodicWork(
                "my_periodic_api_request",
                ExistingPeriodicWorkPolicy.KEEP,
                myWorker
            )

        WorkManager.getInstance(getApplication())
            .getWorkInfoByIdLiveData(myWorker.id)
            .observe(this) { workInfo ->
                if ((workInfo != null) &&
                    (workInfo.state == WorkInfo.State.ENQUEUED)
                ) {
                    val myOutputData = workInfo.outputData.getString("KEY_DATA")
                    Timber.d("Data :: $myOutputData")
                }
            }
    }

    override fun onResume() {
        super.onResume()
        if (prefRepository.getLoggedIn() == true) {
            mViewModel.checkSubscription()
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("onBackPressedDispatcher.onBackPressed()"))
    override fun onBackPressed() {
        val fragmentManager: FragmentManager = supportFragmentManager

        val currentFragment: Fragment? =
            fragmentManager.findFragmentById(com.willow.android.R.id.fl_main_holder)
        if (currentFragment is ExploreFragment) {
            finishAffinity()
        }else{
            onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Timber.d("onKeyDown: : $keyCode")

        if (event?.action == KeyEvent.ACTION_DOWN) {
            Timber.d("Focussed View:: " + window.currentFocus)

//            EventBus.getDefault().post(KeyPressedEvent(keyCode,event))

            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
//                    if (window.currentFocus?.id == -1 || window.currentFocus?.id == 3)
//                        EventBus.getDefault().post(KeyPressedEvent(keyCode, event))
                }

                KeyEvent.KEYCODE_DPAD_RIGHT -> {
//                    if(window.currentFocus?.id == -1 ||window.currentFocus?.id == 3)
//                        EventBus.getDefault().post(KeyPressedEvent(keyCode,event))

                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }



}