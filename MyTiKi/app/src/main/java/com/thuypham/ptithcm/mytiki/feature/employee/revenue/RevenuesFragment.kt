package com.thuypham.ptithcm.mytiki.feature.employee.revenue

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentRevenuesBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.feature.employee.revenue.adaper.RevenueAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.RevenueViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class RevenuesFragment : BaseFragment<FragmentRevenuesBinding>() {

    override val layoutId: Int = R.layout.fragment_revenues
    private val revenueViewModel: RevenueViewModel by viewModel()
    private val revenueAdapter by lazy {
        RevenueAdapter { dateStr -> showRevenueByDate(dateStr) }
    }

    private fun showRevenueByDate(dateStr: String) {
        val bundle = bundleOf(Constant.REVENUE_DATE to dateStr)
        findNavController().navigate(R.id.ordersFragment2, bundle)
    }

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_back_only,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        getString(R.string.revenue)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }
                }
            })
    }

    private var date: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    override fun initView() {
        super.initView()
        val calendar = Calendar.getInstance()
        date = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        revenueViewModel.getAllRevenueInMonth(date, month + 1, year)

        /* init recyclerview */
        viewBinding.rvRevenue.adapter = revenueAdapter
        viewBinding.rvRevenue.setHasFixedSize(true)
        viewBinding.rvRevenue.setItemViewCacheSize(20)
    }

    override fun setEvents() {
        super.setEvents()
        viewBinding.swRefreshRevenue.setOnRefreshListener {
            revenueViewModel.getAllRevenueInMonth(date, month + 1, year)
            viewBinding.swRefreshRevenue.isRefreshing = false
        }
    }

    override fun bindViewModel() {
        super.bindViewModel()
        revenueViewModel.listRevenue.observe(viewLifecycleOwner) {
            revenueAdapter.setRevenueList(it)
        }

        revenueViewModel.networkListRevenue.observe(viewLifecycleOwner) {
            viewBinding.progressRevenue.visibility =
                if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        }
    }

}