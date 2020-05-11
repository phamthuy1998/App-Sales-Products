package com.thuypham.ptithcm.mytiki.feature.employee.main

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.ActivityType
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.databinding.ActivityMainEmployeeBinding
import com.thuypham.ptithcm.mytiki.ext.setupToolbar

class HomeEmployeeActivity : BaseActivity<ActivityMainEmployeeBinding>() {

    override val layoutId: Int = R.layout.activity_main_employee

    override var toolbarViewParentId: Int = R.id.clMain

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
    }

    override fun updateUser() {
        super.updateUser()
        viewBinding.isAdmin = user?.role == 3L
    }
    override fun bindView() {
        super.bindView()
        drawerLayout = viewBinding.drawerMain
    }

    private fun setUpToolbar() {
        setupToolbar(
            toolbarLayoutId = R.layout.toolbar_main,
            rootViewId = toolbarViewParentId,
            hasBack = false,
            elevation = 2f,
            messageQueue = toolbarFunctionQueue {
                func { _, toolbar ->
                    toolbar?.findViewById<AppCompatImageButton>(R.id.btnOption)
                        ?.setOnClickListener {
                            toggleDrawer()
                        }
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        getString(R.string.bot_nav_home)
                }
            })
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.llProduct -> onNavigateOtherActivity(ActivityType.PRODUCT)
            R.id.llSlide -> onNavigateOtherActivity(ActivityType.SLIDE)
            R.id.llCategory -> onNavigateOtherActivity(ActivityType.CATEGORY)
            R.id.llAcc -> onNavigateOtherActivity(ActivityType.ACCOUNT)
            R.id.llRevenue -> onNavigateOtherActivity(ActivityType.REVENUE)
            R.id.llOrder -> onNavigateOtherActivity(ActivityType.ORDER)
        }
    }

}
