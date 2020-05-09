package com.thuypham.ptithcm.mytiki.feature.authentication

import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.databinding.FragmentSignInAndSingUpBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController


class SignInUpFragment : BaseFragment<FragmentSignInAndSingUpBinding>() {

    override val layoutId: Int = R.layout.fragment_sign_in_and_sing_up

    override fun initView() {
        super.initView()
        (activity as BaseActivity<*>).removeToolbar()
        viewBinding.fragment = this
    }

    override fun bindViewModel() {
        super.bindViewModel()
    }

    fun onClickShowSignInFragment() {
        findNavController().navigate(R.id.fragmentSignIn)
    }

    fun onClickShowSignUpFragment() {
        findNavController().navigate(R.id.fragmentSignUp)
    }
}