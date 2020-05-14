package com.thuypham.ptithcm.mytiki.feature.employee.product

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentProductsBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.feature.employee.product.adapter.ProductEmployeeAdapter
import com.thuypham.ptithcm.mytiki.util.Constant.PRODUCT
import com.thuypham.ptithcm.mytiki.viewmodel.ProductViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductsFragment : BaseFragment<FragmentProductsBinding>() {

    override val layoutId: Int = R.layout.fragment_products
    private val productViewModel: ProductViewModel by viewModel()
    private val productAdapter by lazy {
        ProductEmployeeAdapter { product -> showProductDetail(product) }
    }

    private fun showProductDetail(product: Product?) {
        val bundle = bundleOf(PRODUCT to product)
        findNavController().navigate(R.id.productDetailFragment, bundle)
    }

    override fun initView() {
        super.initView()
        productViewModel.getAllProduct()
        viewBinding.rvProductEmployee.adapter = productAdapter
        viewBinding.rvProductEmployee.setHasFixedSize(true)
        viewBinding.rvProductEmployee.setItemViewCacheSize(20)
    }

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_option,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        getString(R.string.products)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }
                    toolbar?. findViewById < ImageButton >(R.id.btnOption)?.apply {
                        setOnClickListener {  showProductDetail(null)}
                    }
                }
            })
    }

    override fun bindViewModel() {
        super.bindViewModel()
        productViewModel.listAllProducts.observe(viewLifecycleOwner, Observer {
            it?.reverse()
            productAdapter.setProductList(it)
        })
        productViewModel.networkAllProducts.observe(viewLifecycleOwner, Observer {
            viewBinding.progressProduct.visibility =
                if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        })
    }

}
