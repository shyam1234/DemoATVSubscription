package com.willow.android.mobile.views.pages.onboardingPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.willow.android.databinding.OnboardingBallByBallFragmentBinding


class OnboardingBallByBallFragment : Fragment() {
    private lateinit var binding: OnboardingBallByBallFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = OnboardingBallByBallFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.exoloreButton.setOnClickListener {
            activity?.finish()
        }
    }
}