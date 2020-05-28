package com.thuypham.ptithcm.mytiki.feature.employee.revenue

import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.data.Order
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentOrderDetailBinding
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.feature.customer.order.adapter.ProductOrderAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.OrderViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class OrderRevenueDetailFragment : BaseFragment<FragmentOrderDetailBinding>() {

    override val layoutId: Int = R.layout.fragment_order_detail
    private val orderViewModel: OrderViewModel by viewModel()

    private val productAdapter: ProductOrderAdapter by lazy {
        ProductOrderAdapter(arrayListOf(), requireContext())
    }

    private var arrStatusOrder = arrayListOf("")

    private var order: Order? = null
    override fun initView() {
        super.initView()
        order = arguments?.get(Constant.ORDER) as? Order
        viewBinding.order = order
        order?.id?.let { orderViewModel.getOrderDetail(it) }
        viewBinding.rvOrderDetail.adapter = productAdapter

//        initSpinner()
    }

    private fun initSpinner() {
        if (order?.status != 4 || order?.status != 3) {
            arrStatusOrder = arrayListOf(getString(R.string.status_2), getString(R.string.status_3))
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrStatusOrder)
            viewBinding.spOrderStatus.adapter = adapter
            viewBinding.spOrderStatus.setSelection(order?.status?.minus(2) ?: 0)
        } else {
            viewBinding.spOrderStatus.gone()
            viewBinding.btnChangeStatus.gone()
        }
    }

    override fun setEvents() {
        super.setEvents()

        viewBinding.btnChangeStatus.setOnClickListener { changeStatusOrder() }
//        viewBinding.spOrderStatus.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) {}
//
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    order?.status = position+2
//                }
//            }
    }

    private fun changeStatusOrder() {
        order?.status?.plus(1)?.let {
            order?.id?.let { it1 ->
                order?.status = it
                viewBinding.order = order
                orderViewModel.changeStatusOrder(it, it1)
            }
        }
    }

    override fun bindViewModel() {
        super.bindViewModel()

        orderViewModel.listOrderDetail.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                productAdapter.setList(it)
            }
        }
        orderViewModel.networkOrderDetail.observe(viewLifecycleOwner) {
            if (it.status == Status.FAILED)
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
        }
    }
}