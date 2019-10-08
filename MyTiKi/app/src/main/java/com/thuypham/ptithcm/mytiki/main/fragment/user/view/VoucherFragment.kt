package com.thuypham.ptithcm.mytiki.main.fragment.user.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thuypham.ptithcm.mytiki.MainActivity

import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import kotlinx.android.synthetic.main.fragment_continue_shopping.*

class VoucherFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_voucher, container, false)

        return inflater.inflate(R.layout.fragment_continue_shopping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addEvent()
    }

    private fun addEvent() {
        btn_continue_shopping.setOnClickListener {
            val intent = Intent(getActivity(), MainActivity::class.java)
            getActivity()?.startActivity(intent)
            activity?.finish()
        }
    }

}
