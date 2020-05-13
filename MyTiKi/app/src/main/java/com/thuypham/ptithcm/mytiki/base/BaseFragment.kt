package com.thuypham.ptithcm.mytiki.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.thuypham.ptithcm.mytiki.ext.setAutoHideKeyboard

abstract class BaseFragment<ViewBinding : ViewDataBinding> : Fragment() {
    lateinit var viewBinding: ViewBinding
    @get:LayoutRes
    abstract val layoutId: Int
    @get:LayoutRes

    open val toolbarLayoutId: Int = -1

    open lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setAutoHideKeyboard(requireActivity())

        navController = Navigation.findNavController(view)
        viewBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }
        initView()
        setUpToolbar()
        setEvents()
        bindViewModel()
    }


    open fun setUpToolbar() {}
    open fun setEvents() {}
    open fun toolbarFunc(curActivity: AppCompatActivity?, toolbar: Toolbar?) {}
    open fun bindViewModel() {}
    open fun initView() {}

}