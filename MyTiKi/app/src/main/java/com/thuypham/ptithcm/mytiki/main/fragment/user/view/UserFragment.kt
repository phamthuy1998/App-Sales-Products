package com.thuypham.ptithcm.mytiki.main.fragment.user.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.MainActivity
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.SharedPreference
import com.thuypham.ptithcm.mytiki.databinding.UserFragmentBinding
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.help.after
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.EditProfileActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.user_fragment.*
import android.net.Uri
import com.thuypham.ptithcm.mytiki.main.product.activity.FavoriteActivity


class UserFragment : Fragment() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    val userViewModel: UserViewModel by lazy {
        ViewModelProviders
            .of(this)
            .get(UserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = UserFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this@UserFragment
        binding.userViewModel = userViewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        println("co vo on create")
    }

    override fun onResume() {
        super.onResume()
        println("vo onresume")
        //Open Order activity if isShowOrder = true
        checkLoginFirebase()
        addEvent()
    }

    //Check user has logged firebase yet
    // if yes, allow show and click edit user
    fun checkLoginFirebase() {
        val sharedPreference = SharedPreference(requireContext())
        val isLogin = sharedPreference.getValueBoolien(PhysicsConstants.IS_LOGIN, false)

        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            setInforAcc()
            // thay doi noi dung của text user
            ll_infor_not_logged.visibility = View.GONE
            ll_infor_logged.visibility = View.VISIBLE

            println("user fragment dda dang nhap")
            // hiên button đăng xuất
            btn_sign_out.visibility = View.VISIBLE
        } else {
            println("chuwa dang nhap")
            ll_infor_not_logged.visibility = View.VISIBLE
            ll_infor_logged.visibility = View.GONE
            // chưa đăng nhập thì ẩn button đăng xuất
            btn_sign_out.visibility = View.GONE

        }
    }

    private fun setInforAcc() {
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        val email = mUser.email.toString()
        tv_email?.text = email
        val checkVerified = mUser?.isEmailVerified

        println("Co vo đay hk dạ")

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child(PhysicsConstants.NAME).value as String
                if (!name.isEmpty()) tv_user_name?.text = name
                val daycreate =
                    getString(R.string.day_create) + ": " + snapshot.child(PhysicsConstants.DAY_CREATE).value as String
                if (!daycreate.isEmpty()) tv_time_member?.text = daycreate
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun addEvent() {

        var user: FirebaseUser? = mAuth?.getCurrentUser();
        // Check user loged in firebase yet?
        if (user != null) {
            // If you user had loged, the layout of sign in will be disappear
            ll_infor_not_logged.visibility = View.GONE
            btn_sign_out.visibility = View.VISIBLE
        } else {
            ll_infor_not_logged.visibility = View.VISIBLE
            btn_sign_out.visibility = View.GONE
        }

        ll_infor_not_logged.setOnClickListener {
            onOpentSigInUpFragment()
        }

        // order manage
        tv_manage_order.setOnClickListener {
            if (user != null) {
                showOrderFragment()
            } else {
                println("m co vo day k m")
                onOpentSigInUpFragment()
            }
        }

        // If user had logged, when you click layout, EditProfileActivity will be appear
        ll_infor_logged.setOnClickListener {
            onOpenEditProfileFragment()
        }

        //sign out
        btn_sign_out.setOnClickListener {
            clearInforLogin()
            println("dang xuat")
            progress.visibility = View.VISIBLE

            after(1000, process = {
                progress.visibility = View.GONE
                checkLoginFirebase()
            })
            mAuth?.signOut()
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

        // Get help, call phone number
        tv_hot_line.setOnClickListener() {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0373865759"))
            startActivity(intent)
        }

        // view viewed list product
        tv_viewed_products.setOnClickListener() {
            if (user != null) {
                // intent to FavoriteActivity
                val intentFV = Intent(context, FavoriteActivity::class.java)
                intentFV.putExtra("childKey", PhysicsConstants.VIEWED_PRODUCT)
                intentFV.putExtra("nameToolbar", getString(R.string.favorite_product))
                startActivity(intentFV)
            } else {
                // If user haven't login yet, intent to sign in
                val intent = Intent(context, SignInUpActivity::class.java)
                startActivity(intent)

                println("dang nhap xong")
                user = mAuth?.getCurrentUser()

                // intent to FavoriteActivity
                if (user != null) {
                    val intentFV = Intent(context, FavoriteActivity::class.java)
                    intentFV.putExtra("childKey", PhysicsConstants.VIEWED_PRODUCT)
                    intentFV.putExtra("nameToolbar", getString(R.string.viewd_products))
                    startActivity(intentFV)
                }
            }

        }

        // view favorite list product
        tv_favorite_products.setOnClickListener() {
            if (user != null) {
                // intent to FavoriteActivity
                val intentFV = Intent(context, FavoriteActivity::class.java)
                intentFV.putExtra("childKey", PhysicsConstants.FAVORITE_PRODUCT)
                intentFV.putExtra("nameToolbar", getString(R.string.favorite_product))
                startActivity(intentFV)
            } else {
                // If user haven't login yet, intent to sign in
                val intent = Intent(context, SignInUpActivity::class.java)
                startActivity(intent)

                println("dang nhap xong")
                user = mAuth?.getCurrentUser()

                // intent to FavoriteActivity
                if (user != null) {
                    val intentFV = Intent(context, FavoriteActivity::class.java)
                    intentFV.putExtra("childKey", PhysicsConstants.FAVORITE_PRODUCT)
                    intentFV.putExtra("nameToolbar", getString(R.string.favorite_product))
                    startActivity(intentFV)
                }
            }

        }
    }

    fun showOrderFragment() {
        val intent = Intent(getActivity(), OrderActivity::class.java)
        getActivity()?.startActivity(intent)
    }

    // if login fail, we will remove all of infor that you had entered
    // the infor will be remove in SharedPreference
    private fun clearInforLogin() {
        val sharedPreference: SharedPreference = SharedPreference(requireContext())
        sharedPreference.removeValue(PhysicsConstants.EMAIL_OR_PHONE)
        sharedPreference.removeValue(PhysicsConstants.IS_LOGIN)
        sharedPreference.save(PhysicsConstants.IS_LOGIN, false)
    }

    private fun onOpenEditProfileFragment() {
        val intent = Intent(getActivity(), EditProfileActivity::class.java)
        getActivity()?.startActivity(intent)

    }

    fun onOpentSigInUpFragment() {
        val intent = Intent(getActivity(), SignInUpActivity::class.java)
        getActivity()?.startActivity(intent)
    }

}