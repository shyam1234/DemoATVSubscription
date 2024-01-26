package com.willow.android.tv.common.genericDialogBox

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.willow.android.databinding.DialogGenericBinding
import com.willow.android.tv.common.base.BaseFullScreenDialogFragment
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.setBackgroundSelecter

/**
 * An generic dialog fragment : conatin a title , discription text and 2 button.
 * the text is passed through the model class GenericDialogModel.
 * and on click listner is
 */
class GenericDialogFragment(val listner: IGenericDialogClickListener?) :
    BaseFullScreenDialogFragment() {

    private lateinit var binding: DialogGenericBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogGenericBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = getModelData()
        initView()
        initListner()
    }

    private fun initView() {
        binding.buttonPositive.setBackgroundSelecter()
        binding.buttonNegative.setBackgroundSelecter()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.buttonPositive.focusable = View.FOCUSABLE
        } else {
            binding.buttonPositive.isFocusable = true
        }
        binding.buttonPositive.isFocusable = true
    }

    private fun initListner() {
        binding.buttonPositive.setOnClickListener {
            listner?.onPositiveClick(it)
            this@GenericDialogFragment.dismiss()
        }

        binding.buttonNegative.setOnClickListener {
            listner?.onNegativeClick(it)
            this@GenericDialogFragment.dismiss()
        }
    }


    private fun getModelData(): GenericDialogModel? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(
                GlobalConstants.Keys.KEY_DIALOG_MODEL,
                GenericDialogModel::class.java
            )
        } else {
            arguments?.getSerializable(GlobalConstants.Keys.KEY_DIALOG_MODEL) as GenericDialogModel?
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(model: GenericDialogModel?, listner: IGenericDialogClickListener?) =
            GenericDialogFragment(listner).apply {
                arguments = Bundle().apply {
                    putSerializable(GlobalConstants.Keys.KEY_DIALOG_MODEL, model)
                }
            }
    }
}

