package com.thuypham.ptithcm.mytiki.ext

//import android.view.ViewGroup
//import androidx.annotation.LayoutRes
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import com.google.android.material.appbar.AppBarLayout
//import com.sg.base.R
//
//fun AppCompatActivity.addToolbar(
//    @LayoutRes toolbarLayoutId: Int,
//    viewGroup: ViewGroup?,
//    toolbarCallBack: ((activity: AppCompatActivity?, toolbar: Toolbar?) -> Unit)? = null
//) {
//    viewGroup?.findViewById<AppBarLayout>(R.id.appBarLayout)?.apply {
//        viewGroup.removeView(this)
//    }
//
//    val toolbarItem = layoutInflater.inflate(toolbarLayoutId, viewGroup, false) ?: return
//    toolbarItem.stateListAnimator = null
//    viewGroup?.addView(toolbarItem)
//
//    val toolbar = toolbarItem.findViewById<Toolbar>(R.id.toolbar)
//
//    val stateListAnimator = StateListAnimator()
//    stateListAnimator.addState(
//        IntArray(0),
//        ObjectAnimator.ofFloat(toolbar, "elevation", 0f)
//    )
//    toolbar.stateListAnimator = stateListAnimator
//
//    setSupportActionBar(toolbar)
//    supportActionBar?.setDisplayShowTitleEnabled(false)
//    toolbarCallBack?.invoke(this, toolbar)
//}
//
//fun Activity.removeToolbar(viewGroup: ViewGroup?) {
//    viewGroup?.findViewById<AppBarLayout>(R.id.appBarLayout)?.apply {
//        viewGroup.removeView(this)
//    }
//}
//
//fun AppCompatActivity.transparentStatusBar() {
//    window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//}
//
//fun AppCompatActivity.isBlackStatusBar(isBlack: Boolean = false) {
//    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//    when (isBlack) {
//        true -> {
//            window.statusBarColor = resources.getColor(R.color.black, null)
//            window.decorView.systemUiVisibility =
//                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
//        }
//
//        false -> {
//            window.statusBarColor = resources.getColor(R.color.bg_toolbar, null)
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        }
//    }
//
//}