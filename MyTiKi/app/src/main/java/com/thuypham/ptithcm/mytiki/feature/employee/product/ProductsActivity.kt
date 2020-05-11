package com.thuypham.ptithcm.mytiki.feature.employee.product

import androidx.navigation.Navigation
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.databinding.ActivityProductsBinding

class ProductsActivity : BaseActivity<ActivityProductsBinding>() {
    override val layoutId: Int = R.layout.activity_products

    override var toolbarViewParentId: Int = R.id.clProductContainer

    override fun onSupportNavigateUp(): Boolean =
        Navigation.findNavController(this, R.id.frProductNavigator).navigateUp()

}
