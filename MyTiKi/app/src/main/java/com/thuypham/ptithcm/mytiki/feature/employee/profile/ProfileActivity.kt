package com.thuypham.ptithcm.mytiki.feature.employee.profile

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.observe
import com.sg.vivastory.ext.getTxtTrim
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.ActivityProfileBinding
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.util.isPasswordValid
import com.thuypham.ptithcm.mytiki.util.isPhoneValid
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    override val layoutId: Int = R.layout.activity_profile
    override var toolbarViewParentId: Int = R.id.clProfileContainer
    private val userViewModel: UserViewModel by viewModel()

    override fun setEvents() {
        super.setEvents()
        viewBinding.btnSave.setOnClickListener { changeInfo() }
    }

    override fun updateUser() {
        super.updateUser()
        viewBinding.user = user
    }

    override fun setUpToolbar() {
        super.setUpToolbar()
        setupToolbar(
            toolbarLayoutId = R.layout.toolbar_back_only,
            rootViewId = toolbarViewParentId,
            hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        getString(R.string.edit)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { onBackPressed() }
                    }
                }
            })
    }

    private fun changeInfo() {
        var checkTextOk = true
        if (viewBinding.user?.name == "") {
            viewBinding.edtNameEdit.error = getString(R.string.error_input_name_not_entered)
            checkTextOk = false
        }
        if (viewBinding.user?.phone == "") {
            viewBinding.edtPhoneEdit.error = getString(R.string.error_input_name_not_entered)
            checkTextOk = false
        }
        if (!isPhoneValid(viewBinding.user?.phone ?: "")) {
            viewBinding.edtPhoneEdit.error =
                getString(R.string.error_input_phone_not_correct)
            checkTextOk = false
        }
        // If check change password
        if (viewBinding.ckChangePw.isChecked) {
            // if old password is empty
            if (viewBinding.edtOldPass.getTxtTrim() == "") {
                viewBinding.edtOldPass.error =
                    getString(R.string.error_old_passwords_is_empty)
                checkTextOk = false
            }
            // If new password is empty
            if (viewBinding.edtNewPass.getTxtTrim() == "") {
                viewBinding.edtNewPass.error =
                    getString(R.string.error_new_pw_empty)
                checkTextOk = false
            }
            //if retype new password is empty
            if (viewBinding.edtReNewPass.getTxtTrim() == "") {
                viewBinding.edtReNewPass.error =
                    getString(R.string.error_re_new_pw_empty)
                checkTextOk = false
            }
            //if old password is incorrect
            if (viewBinding.edtOldPass.getTxtTrim() != user?.password) {
                viewBinding.edtOldPass.error =
                    getString(R.string.error_old_passwords_incorrect)
                checkTextOk = false
            }
            // if new password is not valid, like not length, too weak...
            if (!isPasswordValid(viewBinding.edtNewPass.getTxtTrim())) {
                viewBinding.edtNewPass.error =
                    getString(R.string.error_new_pw_not_length)
                checkTextOk = false
            }
            // if new password and retype new password is not match
            if (viewBinding.edtNewPass.getTxtTrim() != viewBinding.edtReNewPass.getTxtTrim()) {
                viewBinding.edtReNewPass.error = getString(R.string.error_passwords_do_not_match)
                checkTextOk = false
            }
        }
        if (!checkTextOk) return
        else {
            if (viewBinding.ckChangePw.isChecked)
                viewBinding.user?.password = viewBinding.edtNewPass.getTxtTrim()
            userViewModel.updateProfile(viewBinding.user)
        }
    }

    override fun bindViewModel() {
        super.bindViewModel()
        userViewModel.networkUpdateProfile.observe(this) {
            viewBinding.progressProfile.visibility =
                if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        }
    }

}
