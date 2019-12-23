package com.thuypham.ptithcm.mytiki.main.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thuypham.ptithcm.mytiki.R

class NotificationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//
//        val binding = FragmentHomeBinding.inflate(inflater, container, false)
//        binding.lifecycleOwner = this@HomeFragment
//        binding.viewModel = homeViewModel
        return inflater.inflate(R.layout.notification_fragment, container, false)
    }
}