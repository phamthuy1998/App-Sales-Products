package com.thuypham.ptithcm.mytiki.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.ActivityType
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.databinding.NavigationViewMainBinding
import com.thuypham.ptithcm.mytiki.event.DrawableListener
import com.thuypham.ptithcm.mytiki.ext.gone

class CustomDrawerLayout(context: Context, attrs: AttributeSet, defStyle: Int) :
    DrawerLayout(context, attrs, defStyle) {
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private lateinit var srcActivityType: ActivityType
    private lateinit var navigationBinding: NavigationViewMainBinding
    private var drawableListener: DrawableListener? = null

    init {
        layoutDirection = View.LAYOUT_DIRECTION_RTL
        setBackgroundColor(Color.WHITE)
    }

    override fun isOpen() = isDrawerOpen(GravityCompat.START)

    fun onClick(destActivity: ActivityType) {
        when (destActivity) {
            srcActivityType -> closeDrawer(GravityCompat.START)
            else -> drawableListener?.onNavigateOtherActivity(destActivity)
        }
    }

    fun setUser(user: User?){
        if (user?.role != 3L) navigationBinding.btnAccounts.gone()
        navigationBinding.user = user
    }

    fun addNavigationView(
        activity: BaseActivity<*>,
        srcActivityType: ActivityType,
        listener: DrawableListener?
    ) {
        navigationBinding = DataBindingUtil.inflate(
            activity.layoutInflater,
            R.layout.navigation_view_main, this, false
        )
        // Not admin, hide accounts

        drawableListener = listener
        navigationBinding.parentView = this
        addView(navigationBinding.root)
        this.srcActivityType = srcActivityType
    }

    fun toggleDrawer() {
        if (isDrawerOpen(GravityCompat.START))
            closeDrawer(GravityCompat.START)
        else
            openDrawer(GravityCompat.START)
    }

    fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnLogOut -> drawableListener?.onLogout()
            R.id.btnCloseDraw -> closeDrawer(GravityCompat.START)
        }
    }
}