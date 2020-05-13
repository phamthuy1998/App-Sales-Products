package com.thuypham.ptithcm.mytiki.feature.authentication

import android.app.AlertDialog
import android.os.Build
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.sg.vivastory.ext.getTxtTrim
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.databinding.FragmentSignUpBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.util.DatePickerFragment
import com.thuypham.ptithcm.mytiki.util.isEmailValid
import com.thuypham.ptithcm.mytiki.util.isPasswordValid
import com.thuypham.ptithcm.mytiki.util.isPhoneValid
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_sign_up.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {
    override val layoutId: Int = R.layout.fragment_sign_up
    private val userViewModel: UserViewModel by viewModel()

    private var isClickSignUp = false
    override fun initView() {
        super.initView()
        viewBinding.fragment = this
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
                        getString(R.string.sign_up)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onClickSignUp() {
        var checkText = true
        val nameEdt = edt_name_sign_up.getTxtTrim()
        val phoneEdt = edt_phone_sign_up.getTxtTrim()
        val emailEdt = edt_email_sign_up.getTxtTrim()
        val passwordEdt = edt_pasword_sign_up.getTxtTrim()
        val genderCheck = rad_male.isChecked
        val birthDayEdt = edt_birthday_sign_up.getTxtTrim()

        if (!isPhoneValid(phoneEdt)) {
            edt_phone_sign_up.error =
                getString(R.string.error_input_phone_not_correct)
            edt_phone_sign_up.requestFocus()
            checkText = false
        }
        if (!isEmailValid(emailEdt)) {
            checkText = false
            edt_email_sign_up.error = getString(R.string.error_input_email_not_correct)
            edt_email_sign_up.requestFocus()
        }
        if (!isPasswordValid(passwordEdt)) {
            checkText = false
            edt_pasword_sign_up.error = getString(R.string.error_input_pw_not_length)
            edt_pasword_sign_up.requestFocus()
        }

        if (checkText) {
            // get time current to write into time create acc
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val dayCreate = current.format(formatter)

            val user = User().apply {
                name = nameEdt
                phone = phoneEdt
                email = emailEdt
                password = passwordEdt
                birthday = birthDayEdt
                gender = genderCheck
                daycreate = dayCreate
                active = false
                del = false
            }
            userViewModel.register(user)
            isClickSignUp = true
        }
    }

    override fun setEvents() {
        super.setEvents()
        viewBinding.edtBirthdaySignUp.setOnClickListener {
            showCalendar()
        }
    }

    override fun bindViewModel() {
        super.bindViewModel()
        userViewModel.networkRegister.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.RUNNING -> {
                    viewBinding.progressSignUp.visible()
                }
                Status.SUCCESS -> {
                    viewBinding.progressSignUp.gone()
                    if (isClickSignUp) {
                        showDialogRegisterSuccess()
                        isClickSignUp = false
                    }
                }
                Status.FAILED -> {
                    viewBinding.progressSignUp.gone()
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    private fun showDialogRegisterSuccess() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        with(builder)
        {
            setMessage(getString(R.string.dialogRegister))
            setPositiveButton(getString(R.string.dialogOk)) { dialog, _ ->
                findNavController().navigate(R.id.fragmentSignIn)
                dialog.dismiss()
            }
            show()
        }
    }

    // Show calendar to select birthday
    private fun showCalendar() {
        DatePickerFragment().show(
            requireActivity().supportFragmentManager,
            "Choose a date of birth"
        )
    }

}
