package com.thuypham.ptithcm.mytiki.main.fragment.user.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.EditProfileActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.user_fragment.*
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.activity.CartActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.activity.AddressActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.activity.OrderActivity
import com.thuypham.ptithcm.mytiki.main.product.activity.FavoriteActivity
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.ll_cart.*
import kotlinx.android.synthetic.main.no_wifi.*
import pub.devrel.easypermissions.EasyPermissions


class UserFragment : Fragment() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    val userViewModel: UserViewModel by lazy {
        ViewModelProviders
            .of(this)
            .get(UserViewModel::class.java)
    }

    private var haveRecieved = 0
    private var shipping = 0


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
        clearInforLogin()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()
        //Open Order activity if isShowOrder = true
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            ll_no_wifi_user.visibility = View.GONE
            checkLoginFirebase()
            setIconOrderNumber()
            getCartCount()
        } else {
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT)
                .show()
            ll_no_wifi_user.visibility = View.VISIBLE
        }
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

            // hiên button đăng xuất
            btn_sign_out.visibility = View.VISIBLE
        } else {
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
        val checkVerified = mUser.isEmailVerified

        if (mUserReference != null) {
            mUserReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child(PhysicsConstants.NAME).value as String
                    if (!name.isEmpty()) tv_user_name?.text = name
                    if (isAdded()) {
                        val daycreate =
                            getString(R.string.day_create) + ": " + snapshot.child(PhysicsConstants.DAY_CREATE).value as String
                        if (!daycreate.isEmpty())
                            tv_time_member?.text = daycreate
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    // get cart count
    private fun getCartCount() {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            val uid = user!!.uid
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.CART)
                .child(uid)
            var cartCount = 0

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        cartCount = 0
                        for (ds in dataSnapshot.children) {
                            if (ds.exists()) {
                                cartCount++
                            }
                        }
                        if (cartCount > 0 && tv_number_cart != null) {
                            tv_number_cart.visibility = View.VISIBLE
                            tv_number_cart.text = cartCount.toString()
                        } else if (tv_number_cart != null) {
                            tv_number_cart.visibility = View.GONE
                        }
                    } else if (tv_number_cart != null) {
                        tv_number_cart.visibility = View.GONE
                        cartCount = 0
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
        } else {
            tv_number_cart.visibility = View.GONE
        }
    }

    private fun addEvent() {
        ll_cart_number.setOnClickListener() {
            val user: FirebaseUser? = mAuth?.getCurrentUser();
            if (user != null) {
                val intentCart = Intent(context, CartActivity::class.java)
                startActivity(intentCart)
            } else {
                val intentCart = Intent(context, SignInUpActivity::class.java)
                startActivity(intentCart)
            }
        }

        btn_try_connect.setOnClickListener() {
            view?.let { it1 -> onViewCreated(it1, null) }
        }

        var user: FirebaseUser? = mAuth?.getCurrentUser();
        // Check user loged in firebase yet?
        if (user != null) {
            // If you user had loged, the layout of sign in will be disappear
            ll_infor_not_logged.visibility = View.GONE
            btn_sign_out.visibility = View.VISIBLE
        } else {
            tv_num_of_list_order_shipping_user.visibility = View.GONE
            tv_num_order_recieved_user.visibility = View.GONE
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
            mAuth?.signOut()
            val intent = Intent(context, MainActivity::class.java)
            ActivityCompat.finishAffinity(requireActivity())
            startActivity(intent)
        }

        // Get help, call phone number
        tv_hot_line.setOnClickListener() {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.CALL_PHONE
                    )
                ) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.CALL_PHONE), targetRequestCode
                    )

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0373865759"))
                startActivity(intent)
            }
        }

        // show list address
        tv_address.setOnClickListener() {
            val intent = Intent(context, AddressActivity::class.java)
            intent.putExtra("userAddress", getString(R.string.list_of_addresses))
            startActivity(intent)
        }

        // view viewed list product
        tv_viewed_products.setOnClickListener() {
            if (user != null) {
                // intent to FavoriteActivity
                val intentFV = Intent(context, FavoriteActivity::class.java)
                intentFV.putExtra("childKey", PhysicsConstants.VIEWED_PRODUCT)
                intentFV.putExtra("nameToolbar", getString(R.string.viewd_products))
                startActivity(intentFV)
            } else {
                // If user haven't login yet, intent to sign in
                val intent = Intent(context, SignInUpActivity::class.java)
                startActivity(intent)

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

        tv_received_order.setOnClickListener {
            if (user != null) {
                val intent = Intent(context, OrderActivity::class.java)
                intent.putExtra("type_order", 1)
                startActivity(intent)
            } else {
                val intent = Intent(context, SignInUpActivity::class.java)
                startActivity(intent)
            }
        }

        tv_order_waiting_shiping.setOnClickListener {
            if (user != null) {
                val intent = Intent(context, OrderActivity::class.java)
                intent.putExtra("type_order", 2)
                startActivity(intent)
            } else {
                val intent = Intent(context, SignInUpActivity::class.java)
                startActivity(intent)
            }
        }

        tv_order_success.setOnClickListener {
            if (user != null) {
                val intent = Intent(context, OrderActivity::class.java)
                intent.putExtra("type_order", 3)
                startActivity(intent)
            } else {
                val intent = Intent(context, SignInUpActivity::class.java)
                startActivity(intent)
            }
        }

        tv_order_canceled.setOnClickListener {
            if (user != null) {
                val intent = Intent(context, OrderActivity::class.java)
                intent.putExtra("type_order", 4)
                startActivity(intent)
            } else {
                val intent = Intent(context, SignInUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setIconOrderNumber() {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            val uid = user.uid
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.ORDER)
                .child(uid)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        haveRecieved = 0
                        shipping = 0
                        for (ds in dataSnapshot.children) {
                            if (ds.exists()) {
                                val id = ds.child(PhysicsConstants.ORDER_ID).value as String?
                                val date = ds.child(PhysicsConstants.ORDER_DATE).value as String?
                                val price = ds.child(PhysicsConstants.ORDER_PRICE).value as Long?
                                val status = ds.child(PhysicsConstants.ORDER_STATUS).value as Long?
                                if (id != null && date != null && price != null && status != null) {
                                    if (status == 1.toLong()) {
                                        haveRecieved++
                                    } else if (status == 2.toLong()) {
                                        shipping++
                                    }
                                }
                            }
                        }
                        if (haveRecieved > 0 && tv_num_order_recieved_user != null) {
                            tv_num_order_recieved_user.visibility = View.VISIBLE
                            tv_num_order_recieved_user.setText(haveRecieved.toString())
                        } else if (tv_num_order_recieved_user != null) {
                            tv_num_order_recieved_user.visibility = View.GONE
                        }
                        if (shipping > 0 && tv_num_of_list_order_shipping_user != null) {
                            tv_num_of_list_order_shipping_user.visibility = View.VISIBLE
                            tv_num_of_list_order_shipping_user.setText(shipping.toString())
                        } else if (tv_num_of_list_order_shipping_user != null) {
                            tv_num_of_list_order_shipping_user.visibility = View.GONE
                        }
                    } else if (tv_num_of_list_order_shipping_user != null && tv_num_order_recieved_user != null) {
                        tv_num_of_list_order_shipping_user.visibility = View.GONE
                        tv_num_order_recieved_user.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
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