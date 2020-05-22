package com.thuypham.ptithcm.mytiki.feature.admin.acc

import android.app.AlertDialog
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.observe
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.databinding.FragmentAccountDetailBinding
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.AccountViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountDetailFragment : BaseFragment<FragmentAccountDetailBinding>() {

    override val layoutId: Int = R.layout.fragment_account_detail

    private val accViewModel: AccountViewModel by viewModel()

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_option,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        accViewModel.user.value?.name ?: getString(R.string.btnAdd)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }

                    toolbar?.findViewById<ImageButton>(R.id.btnOption)?.apply {
                        if (accViewModel.user.value?.id != null) {
                            visibility = View.VISIBLE
                            setImageResource(R.drawable.ic_del)
                        } else visibility = View.INVISIBLE
                        setOnClickListener { confirmDelAcc() }
                    }
                }
            })
    }

    private fun confirmDelAcc() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        with(builder)
        {
            setMessage(getString(R.string.dialogDelProduct))
            setPositiveButton(getString(R.string.dialogOk)) { dialog, _ ->
                accViewModel.user.value?.let { accViewModel.delAccount(it) }
                activity?.onBackPressed()
                dialog.dismiss()
            }
            setNegativeButton(getString(R.string.dialogCancel)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }


    override fun initView() {
        super.initView()
        accViewModel.user.value = arguments?.get(Constant.USER) as? User
        viewBinding.isAdd = accViewModel.user.value?.id == null
    }

    override fun bindViewModel() {
        super.bindViewModel()

        accViewModel.userCreated.observe(viewLifecycleOwner) {

        }
        accViewModel.userUpdated.observe(viewLifecycleOwner) {

        }
        accViewModel.networkUpdateAcc.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.RUNNING -> {
                    viewBinding.progressAcc.visible()
                    viewBinding.btnAddAcc.isEnabled = false
                }
                Status.SUCCESS -> {
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                    viewBinding.isAdd = false
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.addSuccess),
                        Toast.LENGTH_LONG
                    ).show()
                }
                Status.LOADING_PROCESS -> {
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                }
                Status.FAILED -> {
                    Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_LONG).show()
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                }
            }

        }

        accViewModel.networkCreateAcc.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.RUNNING -> {
                    viewBinding.progressAcc.visible()
                    viewBinding.btnAddAcc.isEnabled = false
                }
                Status.SUCCESS -> {
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                    viewBinding.isAdd = false
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.addSuccess),
                        Toast.LENGTH_LONG
                    ).show()
                }
                Status.LOADING_PROCESS -> {
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                }
                Status.FAILED -> {
                    Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_LONG).show()
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                }
            }
        }
    }

}
