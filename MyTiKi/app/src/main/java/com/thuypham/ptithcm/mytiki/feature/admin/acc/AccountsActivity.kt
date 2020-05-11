package com.thuypham.ptithcm.mytiki.feature.admin.acc

import androidx.navigation.Navigation
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.databinding.ActivityOrdersBinding

class AccountsActivity : BaseActivity<ActivityOrdersBinding>()  {

    override val layoutId: Int = R.layout.activity_accounts

    override var toolbarViewParentId: Int = R.id.clAccountContainer

    override fun onSupportNavigateUp(): Boolean =
        Navigation.findNavController(this, R.id.frAccountNavigator).navigateUp()
}
