package com.thuypham.ptithcm.mytiki.feature.employee.order

import androidx.navigation.Navigation
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.databinding.ActivityOrdersBinding

class OrdersActivity : BaseActivity<ActivityOrdersBinding>()  {

    override val layoutId: Int = R.layout.activity_orders

    override var toolbarViewParentId: Int = R.id.clOrderContainer

    override fun onSupportNavigateUp(): Boolean =
        Navigation.findNavController(this, R.id.frOrderNavigator).navigateUp()
}
