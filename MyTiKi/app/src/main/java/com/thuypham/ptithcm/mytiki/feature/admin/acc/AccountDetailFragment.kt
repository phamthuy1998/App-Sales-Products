package com.thuypham.ptithcm.mytiki.feature.admin.acc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thuypham.ptithcm.mytiki.R

/**
 * A simple [Fragment] subclass.
 */
class AccountDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_detail, container, false)
    }

}
