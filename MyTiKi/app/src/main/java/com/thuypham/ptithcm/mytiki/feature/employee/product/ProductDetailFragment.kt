package com.thuypham.ptithcm.mytiki.feature.employee.product

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.databinding.FragmentProductDetailBinding
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.util.Constant.PRODUCT_ID
import com.thuypham.ptithcm.mytiki.viewmodel.ProductViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>() {


    private val productViewModel: ProductViewModel by viewModel()

    override val layoutId: Int =R.layout.fragment_product_detail
    private var productID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productID = arguments?.get(PRODUCT_ID) as? String
        productID?.let { productViewModel.getProductByID(it) }
    }

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_option,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        productViewModel.product.value?.name ?: getString(R.string.btnAdd)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }

                    toolbar?.findViewById<ImageButton>(R.id.btnOption)?.apply {
                        if (productViewModel.product.value?.id != null) {
                            visibility = View.VISIBLE
                            setImageResource(R.drawable.ic_del)
                        } else visibility =  View.INVISIBLE
                        setOnClickListener { confirmDelItem() }
                    }

                }
            })
    }

    private fun confirmDelItem() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun bindViewModel() {
        super.bindViewModel()

    }


}
