package com.thuypham.ptithcm.mytiki.feature.authentication

import androidx.navigation.Navigation
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.databinding.ActivityAuthBinding

class AuthActivity : BaseActivity<ActivityAuthBinding>() {

    override val layoutId: Int = R.layout.activity_auth

    override var toolbarViewParentId: Int = R.id.clAuthContainer

    override fun onSupportNavigateUp(): Boolean =
        Navigation.findNavController(this, R.id.frAuthenticationNavigator).navigateUp()

}
