package com.thuypham.ptithcm.mytiki.feature.customer.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.MyFragmentPagerAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.category.CategoryFragment
import com.thuypham.ptithcm.mytiki.feature.customer.home.HomeFragment
import com.thuypham.ptithcm.mytiki.feature.customer.search.SearchFragment
import com.thuypham.ptithcm.mytiki.feature.customer.user.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                botNavigation.selectedItemId =
                    R.id.bot_nav_home
            }
            1 -> {
                botNavigation.selectedItemId =
                    R.id.bot_nav_category
            }
            2 -> {
                botNavigation.selectedItemId =
                    R.id.bot_nav_search
            }
            3 -> {
                botNavigation.selectedItemId =
                    R.id.bot_nav_user
            }
        }
    }

    private val homeFragment by lazy {
        HomeFragment()
    }
    private val searchFragment by lazy {
        SearchFragment()
    }
    private val categoryFragment by lazy {
        CategoryFragment()
    }
    private val userFragment by lazy {
        UserFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPager()
       addEvents()
        viewPagerMain.addOnPageChangeListener(this)
    }

    private fun addEvents() {
        val checkShowSearch = intent.getBooleanExtra("search", false)
        if(checkShowSearch){
            viewPagerMain.currentItem = 2
        }

        botNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bot_nav_home -> {
                    viewPagerMain.currentItem = 0
                    true
                }
                R.id.bot_nav_category -> {
                    viewPagerMain.currentItem = 1
                    true
                }
                R.id.bot_nav_search -> {
                    viewPagerMain.currentItem = 2
                    true
                }
                R.id.bot_nav_user -> {
                    viewPagerMain.currentItem = 3
                    true
                }
                else -> false
            }
        }
    }

    private fun initPager() {
        val viewPagerAdapter =
            MyFragmentPagerAdapter(
                supportFragmentManager
            )
        viewPagerAdapter.addFragment(homeFragment, "Home fragment")
        viewPagerAdapter.addFragment(categoryFragment, "Category fragment")
        viewPagerAdapter.addFragment(searchFragment, "Search fragment")
        viewPagerAdapter.addFragment(userFragment, "User fragment")
        viewPagerMain.adapter = viewPagerAdapter
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 101) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("Tag", "Xin cap quyen thanh cong")
            } else
                Log.d("Tag", "Xin cap quyen that bai")

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun showCategoryFragment(view: View) {
        viewPagerMain.currentItem = 1
    }

    fun showSearchFragment(view: View) {
        viewPagerMain.currentItem = 2
    }
}
