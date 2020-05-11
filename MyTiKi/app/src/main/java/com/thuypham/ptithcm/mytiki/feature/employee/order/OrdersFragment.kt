package com.thuypham.ptithcm.mytiki.feature.employee.order

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentOrdersBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
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
        OrderEmployeeAdapter{ orderId -> showCategoryDetail(orderId) }
    }

    private fun showCategoryDetail(orderId: String) {
        val bundle = bundleOf(Constant.ORDER_ID to orderId)
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
            orderAdapter.setOrderList(it)
        }

        orderViewModel.networkListOrder.observe(viewLifecycleOwner){
            viewBinding.progressOrder.visibility =
                if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        }
    }

}
