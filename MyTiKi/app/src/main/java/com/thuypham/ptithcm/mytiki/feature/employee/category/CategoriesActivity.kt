package com.thuypham.ptithcm.mytiki.feature.employee.category

import androidx.navigation.Navigation
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.databinding.ActivityCategoriesBinding

class CategoriesActivity : BaseActivity<ActivityCategoriesBinding>()  {

    override val layoutId: Int = R.layout.activity_categories

    override var toolbarViewParentId: Int = R.id.clCategoryContainer

    override fun onSupportNavigateUp(): Boolean =
        Navigation.findNavController(this, R.id.frProductNavigator).navigateUp()
}
