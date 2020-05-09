package com.thuypham.ptithcm.mytiki.ext

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import com.google.android.material.appbar.AppBarLayout
import com.thuypham.ptithcm.mytiki.R

fun AppCompatActivity.setupToolbar(
    @LayoutRes toolbarLayoutId: Int?, @IdRes rootViewId: Int?, hasBack: Boolean = false,
    messageQueue: ArrayList<(activity: AppCompatActivity?, toolbar: Toolbar?) -> Unit>? = null,
    elevation: Float = 2F,
    scrollFlags: Int = -1
) {
    var toolbarItem: View? = null
    if (toolbarLayoutId != null && rootViewId != null) {
        findViewById<ViewGroup>(rootViewId).apply {
            this.removeView(this.children.find { it.id == R.id.appBarLayout })
            toolbarItem = layoutInflater.inflate(toolbarLayoutId, this, false)
            this.addView(toolbarItem)
        }
    }
    val toolbar = toolbarItem?.findViewById<Toolbar>(R.id.toolbar)

    if (scrollFlags != -1) {
        val params = toolbar?.layoutParams as? AppBarLayout.LayoutParams
        params?.scrollFlags = scrollFlags
    }

    when (hasBack) {
        true -> toolbar?.findViewById<AppCompatImageButton>(R.id.ivBack)?.visibility =
            View.VISIBLE
        false -> toolbar?.findViewById<AppCompatImageButton>(R.id.ivBack)?.visibility =
            View.GONE
    }
    this.setSupportActionBar(toolbar)
    //end
    messageQueue?.forEach {
        it.invoke(this, toolbar)
    }

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        val layoutAppBar = toolbar?.parent as? AppBarLayout
        val stateListAnimator = StateListAnimator()
        stateListAnimator.addState(
            IntArray(0),
            ObjectAnimator.ofFloat(layoutAppBar, "elevation", elevation)
        )
        layoutAppBar?.stateListAnimator = stateListAnimator
    }
    toolbar?.requestLayout()
}


fun Activity.removeToolbar(viewGroup: ViewGroup?) {
    viewGroup?.findViewById<AppBarLayout>(R.id.appBarLayout)?.apply {
        viewGroup.removeView(this)
    }
}

fun AppCompatActivity.transparentStatusBar() {
    window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}