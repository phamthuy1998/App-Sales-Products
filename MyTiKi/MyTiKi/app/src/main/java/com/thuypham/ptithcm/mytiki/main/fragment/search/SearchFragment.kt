package com.thuypham.ptithcm.mytiki.main.fragment.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thuypham.ptithcm.mytiki.MainActivity
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import kotlinx.android.synthetic.main.search_fragment.*
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants


class SearchFragment : Fragment() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        addEvent()
    }


    private fun addProductSeen() {
        val mUser = mAuth!!.currentUser;
        mDatabaseReference = mDatabase!!.reference.child(PhysicsConstants.USERS)
        val productSeen = mDatabaseReference!!.child(mUser!!.getUid()).child(PhysicsConstants.VIEWED_PRODUCT)
        productSeen.child(PhysicsConstants.PRODUCT_ID).setValue("Ln03ff43JZkvpcaSm12")
    }

    private fun addEvent() {
        btn_cancel_search.setOnClickListener {
            val intent = Intent(getActivity(), MainActivity::class.java)
            getActivity()?.startActivity(intent)
            activity?.finish()
        }
    }
}