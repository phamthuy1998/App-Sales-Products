package com.thuypham.ptithcm.mytiki

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.google.firebase.iid.FirebaseInstanceId
import com.thuypham.ptithcm.mytiki.main.fragment.category.fragment.CategoryFragment
import com.thuypham.ptithcm.mytiki.main.fragment.home.fragment.HomeFragment
import com.thuypham.ptithcm.mytiki.main.fragment.notification.NotificationFragment
import com.thuypham.ptithcm.mytiki.main.fragment.search.SearchFragment
import com.thuypham.ptithcm.mytiki.main.fragment.user.adapter.MyFragmentPagerAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.user.view.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                botNavigation.selectedItemId = R.id.bot_nav_home
            }
            1 -> {
                botNavigation.selectedItemId = R.id.bot_nav_category
            }
            2 -> {
                botNavigation.selectedItemId = R.id.bot_nav_search
            }
            3 -> {
                botNavigation.selectedItemId = R.id.bot_nav_notification
            }
            4 -> {
                botNavigation.selectedItemId = R.id.bot_nav_user
            }
        }
    }

    // set icon selected: home
    var idMenuSelected: Int = R.id.bot_nav_home
    val homeFragment by lazy {
        HomeFragment()
    }
    val searchFragment by lazy {
        SearchFragment()
    }
    val categoryFragment by lazy {
        CategoryFragment()
    }
    val notificationFragment by lazy {
        NotificationFragment()
    }
    val userFragment by lazy {
        UserFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val viewPagerAdapter = MyFragmentPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(homeFragment, "Home fragment")
        viewPagerAdapter.addFragment(categoryFragment, "Category fragment")
        viewPagerAdapter.addFragment(searchFragment, "Search fragment")
        viewPagerAdapter.addFragment(notificationFragment, "Notification fragment")
        viewPagerAdapter.addFragment(userFragment, "User fragment")

        viewPagerMain.adapter = viewPagerAdapter

        //viewPagerMain.setOffscreenPageLimit(1)

        viewPagerMain.addOnPageChangeListener(this)

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
                R.id.bot_nav_notification -> {
                    viewPagerMain.currentItem = 3
                    true
                }
                R.id.bot_nav_user -> {
                    viewPagerMain.currentItem = 4
                    true
                }
                else -> false
            }
        }

//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener {
//                if (!it.isComplete) {
//                    Log.w("Tag", "Khong thanh cong", it.exception)
//                } else {
//                    val token = it.result?.token
//                    Log.d("Tag", "Token=${token}")
//
//                }
//            }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// android 6.0
//            val permisson = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//            requestPermissions(permisson, 101)
//        }
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
}
