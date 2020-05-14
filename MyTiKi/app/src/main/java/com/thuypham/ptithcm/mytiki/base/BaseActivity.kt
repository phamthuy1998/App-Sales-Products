package com.thuypham.ptithcm.mytiki.base

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.event.DrawableListener
import com.thuypham.ptithcm.mytiki.ext.setAutoHideKeyboard
import com.thuypham.ptithcm.mytiki.feature.authentication.AuthActivity
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import com.thuypham.ptithcm.mytiki.widget.CustomDrawerLayout
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseActivity<ViewBinding : ViewDataBinding> : AppCompatActivity(), DrawableListener {
    lateinit var viewBinding: ViewBinding

    @get:LayoutRes
    abstract val layoutId: Int
    @get:LayoutRes
    open val toolbarLayoutId: Int = -1

    abstract var toolbarViewParentId: Int
    private var currentActivityType = ActivityType.HOME
    private var destActivityType = ActivityType.HOME

    open var user: User? = null

    open var drawerLayout: CustomDrawerLayout? = null
        set(value) {
            field = value
            field?.addNavigationView(this, currentActivityType, this)
        }


    private val authViewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel.getCurrentUser()
        viewBinding = DataBindingUtil.setContentView(this, layoutId)
        viewBinding.lifecycleOwner = this
        viewBinding.root.setAutoHideKeyboard(this)
        setUpToolbar()
        bindView()
        bindViewModel()
        setEvents()
    }

    override fun onLogout() {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setMessage(getString(R.string.dialogLogout))
            setPositiveButton(getString(R.string.dialogOk)) { dialog, _ ->
                dialog.dismiss()
                authViewModel.logOut()
                drawerLayout?.closeDrawers()
                startActivity<AuthActivity>()
                finish()
            }
            setNegativeButton(getString(R.string.dialogCancel)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    override fun onNavigateOtherActivity(destActivity: Enum<*>) {
        destActivityType = destActivity as ActivityType
        val intent = Intent(this, destActivityType.value())
        startActivity(intent)
        destActivityType = ActivityType.EMPTY

        if (currentActivityType != ActivityType.HOME)
            finish()
    }

    override fun onResume() {
        super.onResume()
        drawerLayout?.closeDrawers()
    }

    fun removeToolbar() {
        if (toolbarViewParentId != 0) {
            findViewById<ViewGroup>(toolbarViewParentId)?.apply {
                removeView(children.find { it.id == R.id.appBarLayout })
                requestLayout()
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout?.isOpen == true)
            drawerLayout?.toggleDrawer()
        else
            super.onBackPressed()
    }


    fun toggleDrawer() {
        drawerLayout?.toggleDrawer()
    }

    fun closeDrawer() {
        drawerLayout?.closeDrawers()
    }

    open fun setUpToolbar() {}
    open fun toolbarFunc(curActivity: Activity?, toolbar: Toolbar?) {}
    open fun bindView() {}
    open fun setEvents() {}
    open fun updateUser() {}
    open fun bindViewModel() {
        authViewModel.currentUser.observe(this, Observer { user ->
            this.user = user
            drawerLayout?.setUser(user)
            updateUser()
        })
    }

    fun showToast(msg: String, typeToast: Int) {
        Toast.makeText(this, msg, typeToast).show()
    }
}