package com.thuypham.ptithcm.mytiki.feature.authentication

import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.sg.vivastory.ext.getTxtTrim
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentSignInBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.feature.employee.main.HomeEmployeeActivity
import com.thuypham.ptithcm.mytiki.util.isEmailValid
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import org.jetbrains.anko.support.v4.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SignInFragment : BaseFragment<FragmentSignInBinding>() {

    val userViewModel: UserViewModel by sharedViewModel(from = { requireActivity() })
    override val layoutId: Int = R.layout.fragment_sign_in

    override fun initView() {
        super.initView()
        viewBinding.userViewModel = userViewModel
    }

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_back_only,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        getString(R.string.sign_in)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener {
                            findNavController().navigate(R.id.gotoLogin)
                        }
                    }
                }
            })
    }


    override fun setEvents() {
        super.setEvents()
        viewBinding.tvForgotPassword.setOnClickListener { findNavController().navigate(R.id.fragmentForgotPassword) }
        viewBinding.btnSignIn.setOnClickListener {onClickSignIn() }
    }

    private fun onClickSignIn() {
        val emailEdt = viewBinding.edtEmailSignIn.getTxtTrim()
        if (!isEmailValid(emailEdt)) {
            viewBinding.edtEmailSignIn.error =
                getString(R.string.error_input_email_not_correct)
            viewBinding.edtEmailSignIn.requestFocus()
            return
        }
        userViewModel.login(
            viewBinding.edtEmailSignIn.getTxtTrim(),
            viewBinding.edtPasswordSignIn.getTxtTrim()
        )
    }

    override fun bindViewModel() {
        super.bindViewModel()
        userViewModel.userInfoLogin.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                if (user.role == 1L) startActivity<MainActivity>()
                else startActivity<HomeEmployeeActivity>()
                requireActivity().finish()
            }
        })
        userViewModel.networkStateUserLogin.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.RUNNING -> {
                    viewBinding.progressSignIn.visible()
                }
                Status.SUCCESS -> {
                    viewBinding.progressSignIn.gone()
                }
                Status.FAILED -> {
                    viewBinding.progressSignIn.gone()
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
