package com.thuypham.ptithcm.mytiki.feature.employee.slide

import androidx.navigation.Navigation
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.databinding.ActivitySlidesBinding

class SlidesActivity: BaseActivity<ActivitySlidesBinding>()  {

    override val layoutId: Int = R.layout.activity_slides

    override var toolbarViewParentId: Int = R.id.ctlSlideContainer

    override fun onSupportNavigateUp(): Boolean =
        Navigation.findNavController(this, R.id.frSlideNavigator).navigateUp()
}
