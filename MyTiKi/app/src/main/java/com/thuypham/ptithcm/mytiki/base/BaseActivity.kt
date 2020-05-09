package com.thuypham.ptithcm.mytiki.base

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.ext.setAutoHideKeyboard

abstract class BaseActivity<ViewBinding : ViewDataBinding> : AppCompatActivity(){
    lateinit var viewBinding: ViewBinding

    @get:LayoutRes
    abstract val layoutId: Int
    @get:LayoutRes
    open val toolbarLayoutId: Int = -1

    abstract var toolbarViewParentId: Int

//    private val authViewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, layoutId)
        viewBinding.lifecycleOwner = this
        viewBinding.root.setAutoHideKeyboard(this)
        bindView()
        bindViewModel()
        setEvents()
    }

    fun removeToolbar() {
        if (toolbarViewParentId != 0) {
            findViewById<ViewGroup>(toolbarViewParentId)?.apply {
                removeView(children.find { it.id == R.id.appBarLayout })
                requestLayout()
            }
        }
    }

    open fun toolbarFunc(curActivity: Activity?, toolbar: Toolbar?) {}
    open fun bindView() {}
    open fun setEvents() {}
    open fun bindViewModel() {}

}