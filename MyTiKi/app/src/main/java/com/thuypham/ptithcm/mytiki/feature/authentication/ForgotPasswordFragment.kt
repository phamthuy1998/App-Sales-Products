package com.thuypham.ptithcm.mytiki.feature.authentication

import android.app.AlertDialog
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
import com.thuypham.ptithcm.mytiki.databinding.FragmentForgotPasswordBinding
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.util.isEmailValid
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {

    override val layoutId: Int = R.layout.fragment_forgot_password

    private var isClickForgotPW = false
    val userViewModel: UserViewModel by sharedViewModel(from = { requireActivity() })

    override fun initView() {
        super.initView()
        viewBinding.userViewModel = userViewModel
        viewBinding.fragment = this
    }

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_back_only,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        getString(R.string.sign_up)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }
                }
            })
    }

    fun onClickResetPassword() {
        val emailEdt = viewBinding.edtEmailForgotPassword.getTxtTrim()
        if (!isEmailValid(emailEdt)) {
            viewBinding.edtEmailForgotPassword.error =
                getString(R.string.error_input_email_not_correct)
            viewBinding.edtEmailForgotPassword.requestFocus()
            return
        }
        userViewModel.resetPassword(viewBinding.edtEmailForgotPassword.getTxtTrim())
        isClickForgotPW = true
    }

    override fun bindViewModel() {
        super.bindViewModel()
        userViewModel.networkSendMail.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.RUNNING -> {
                    viewBinding.progressForgotPW.visible()
                }
                Status.SUCCESS -> {
                    viewBinding.progressForgotPW.gone()
                    if (isClickForgotPW) {
                        isClickForgotPW = false
                        showDialogSendMailSuccess()
                    }
                }
                Status.FAILED -> {
                    viewBinding.progressForgotPW.gone()
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun showDialogSendMailSuccess() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        with(builder)
        {
            setMessage(getString(R.string.dialogSendMail))
            setPositiveButton(getString(R.string.dialogOk)) { dialog, _ ->
                requireActivity().onBackPressed()
                dialog.dismiss()
            }
            show()
        }
    }
}