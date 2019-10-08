package com.thuypham.ptithcm.mytiki.main.fragment.user.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.thuypham.ptithcm.mytiki.Main.fragment.main.ListOrderFragment
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.main.fragment.user.adapter.MyFragmentPagerAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.user.view.VoucherFragment
import kotlinx.android.synthetic.main.order_activity.*

class OrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_activity)
        val adapter =MyFragmentPagerAdapter(supportFragmentManager)
        adapter.addFragment(ListOrderFragment(), getString(R.string.list_orders))
        adapter.addFragment(VoucherFragment(), getString(R.string.my_vouchers))
        viewPager_order.adapter = adapter
        tab_order.setupWithViewPager(viewPager_order)

    }

    fun onClickBackToUser(view: View) {
        finish()
    }
}