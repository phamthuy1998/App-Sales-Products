package com.thuypham.ptithcm.mytiki.feature.employee.order

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
import com.thuypham.ptithcm.mytiki.data.Order
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentOrdersBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.feature.employee.order.adaper.OrderEmployeeAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.OrderViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class OrdersFragment : BaseFragment<FragmentOrdersBinding>() {

    override val layoutId: Int = R.layout.fragment_orders
    private val orderViewModel: OrderViewModel by viewModel()
    private val orderAdapter by lazy {
        OrderEmployeeAdapter{ order  -> showCategoryDetail(order) }
    }

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_back_only,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text = getString(R.string.orders)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }
                }
            })
    }


    private fun showCategoryDetail(order: Order) {
        val bundle = bundleOf(Constant.ORDER to order)
        findNavController().navigate(R.id.orderDetailFragment, bundle)
    }

    override fun initView() {
        super.initView()
        orderViewModel.getAllOrder()
        viewBinding.rvOrders.adapter = orderAdapter
        viewBinding.rvOrders.setHasFixedSize(true)
        viewBinding.rvOrders.setItemViewCacheSize(20)
    }

    override fun bindViewModel() {
        super.bindViewModel()
        orderViewModel.listOrder.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                orderAdapter.setOrderList(it)
                viewBinding.tvOrderEmpty.gone()
            } else viewBinding.tvOrderEmpty.visible()
        }

        orderViewModel.networkListOrder.observe(viewLifecycleOwner){
            viewBinding.progressOrder.visibility =
                if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        }
    }

}
