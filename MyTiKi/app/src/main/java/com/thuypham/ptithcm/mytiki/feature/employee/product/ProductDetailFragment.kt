package com.thuypham.ptithcm.mytiki.feature.employee.product

import android.os.Bundle
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.databinding.FragmentProductDetailBinding
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

    override fun bindViewModel() {
        super.bindViewModel()

    }


}
