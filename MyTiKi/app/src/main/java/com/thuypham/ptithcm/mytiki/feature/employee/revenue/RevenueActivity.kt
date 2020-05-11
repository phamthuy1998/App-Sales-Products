package com.thuypham.ptithcm.mytiki.feature.employee.revenue

import androidx.navigation.Navigation
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.databinding.ActivityRevenueBinding

class RevenueActivity: BaseActivity<ActivityRevenueBinding>()  {

    override val layoutId: Int = R.layout.activity_revenue

    override var toolbarViewParentId: Int = R.id.ctlRevenueContainer

    override fun onSupportNavigateUp(): Boolean =
        Navigation.findNavController(this, R.id.frRevenueNavigator).navigateUp()
}
