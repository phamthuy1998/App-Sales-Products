package com.thuypham.ptithcm.mytiki.main.fragment.user.login.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thuypham.ptithcm.mytiki.main.fragment.user.adapter.MyFragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_sign_in_and_sing_up.*
import com.thuypham.ptithcm.mytiki.R


class SignInUpFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println("cos khoi taolai view")
        return inflater.inflate(R.layout.fragment_sign_in_and_sing_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("view tao lai")
        val adapter =MyFragmentPagerAdapter(activity!!.supportFragmentManager)
        adapter.addFragment(SignInFragment(), getString(R.string.sign_in))
        adapter.addFragment(SignUpFragment(), getString(R.string.sign_up))
        viewPager_sign_in_up.adapter = adapter
        viewPager_sign_in_up.setOffscreenPageLimit(1)
        tab_sign_in_up.setupWithViewPager(viewPager_sign_in_up)

    }
}