package com.willow.android.mobile.views.popup.livePopup

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.LivePopupActivityBinding
import com.willow.android.mobile.models.pages.LiveSourceModel
import com.willow.android.mobile.models.pages.MultipleLiveSourcesModel

class LiveSourcesDialog : DialogFragment() {
    private lateinit var binding: LivePopupActivityBinding

    var onResult: ((selectedSource: LiveSourceModel) -> Unit)? = null

    companion object {
        private const val LIVE_SOURCES_DATA = "LIVE_SOURCES_DATA"

        fun newInstance(lat: MultipleLiveSourcesModel? = null): LiveSourcesDialog {
            val dialog = LiveSourcesDialog()
            val args = Bundle().apply {
                lat?.let { putSerializable(LIVE_SOURCES_DATA, it) }
            }
            dialog.arguments = args
            return dialog
        }
    }

    /** The system calls this to get the DialogFragment's layout, regardless
    of whether it's being displayed as a dialog or an embedded fragment. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as dialog or embedded fragment
        binding = LivePopupActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    /** The system calls this only when creating the layout in a dialog. */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // receive arguments
        val streamModel = arguments?.getSerializable(LIVE_SOURCES_DATA) as? MultipleLiveSourcesModel

        val categoryAdapter = LivePopupAdapter(this, requireActivity(), streamModel!!.video_sources)
        val categoryLinearLayoutManager = LinearLayoutManager(requireActivity())
        categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        val recycler: RecyclerView = binding.livePopupRecycler
        recycler.layoutManager = categoryLinearLayoutManager
        recycler.adapter = categoryAdapter

        binding.cancelAction.setOnClickListener { dismiss() }
    }

    fun onLiveSourceSelected(selectedSource: LiveSourceModel) {
        onResult?.invoke(selectedSource)
    }
}