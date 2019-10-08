package com.thuypham.ptithcm.mytiki.main.order.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.thuypham.ptithcm.mytiki.R

class AddressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
    }

    fun onClickQuiteAddress(view: View) {finish()}
    fun onClickConfirmOrder(view: View) {
        val intent = Intent(this, ConfirmOrderActivity::class.java)
        startActivity(intent)
    }
}
