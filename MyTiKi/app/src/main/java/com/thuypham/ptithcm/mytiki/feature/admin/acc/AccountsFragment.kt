package com.thuypham.ptithcm.mytiki.feature.admin.acc

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.databinding.FragmentAccountsBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.feature.admin.acc.adapter.AccountAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.AccountViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountsFragment : BaseFragment<FragmentAccountsBinding>() {

    override val layoutId: Int = R.layout.fragment_accounts
    private val accViewModel: AccountViewModel by viewModel()
    private val accountAdapter by lazy {
        AccountAdapter { user -> showAccountDetail(user) }
    }

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_option,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        getString(R.string.categories)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }
                    toolbar?.findViewById<ImageButton>(R.id.btnOption)?.apply {
                        setOnClickListener { showAccountDetail(null) }
                    }
                }
            })
    }

    private fun showAccountDetail(user: User?) {
        val bundle = bundleOf(Constant.USER to user)
        findNavController().navigate(R.id.accountDetailFragment, bundle)
    }

    override fun initView() {
        super.initView()
        accViewModel.getAllAccount()
        viewBinding.rvOrders.adapter = accountAdapter
        viewBinding.rvOrders.setHasFixedSize(true)
        viewBinding.rvOrders.setItemViewCacheSize(20)
    }

    override fun bindViewModel() {
        super.bindViewModel()
        accViewModel.listAccount.observe(viewLifecycleOwner, Observer {
            accountAdapter.setUserList(it)
        })

        accViewModel.networkAcc.observe(viewLifecycleOwner, Observer {
            viewBinding.progressOrder.visibility =
                if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        })
    }

}