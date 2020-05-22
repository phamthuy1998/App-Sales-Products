package com.thuypham.ptithcm.mytiki.feature.customer.category

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.feature.authentication.AuthActivity
import com.thuypham.ptithcm.mytiki.feature.customer.cart.CartActivity
import com.thuypham.ptithcm.mytiki.feature.customer.category.adapter.CategoryAdapter
import com.thuypham.ptithcm.mytiki.viewmodel.CategoryViewModel
import com.thuypham.ptithcm.mytiki.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.category_fragment.*
import kotlinx.android.synthetic.main.ll_cart.*
import kotlinx.android.synthetic.main.no_wifi.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CategoryFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null

    // Category
    private var adapter: CategoryAdapter? = null
    private var categoryList = ArrayList<Category>()

    private val productViewModel: ProductViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryViewModel.getAllCategory()
        productViewModel.getCartCount()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.category_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inIt()
        bindViewModel()

        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            ll_no_wifi_category.visibility = View.GONE
        } else {
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT)
                .show()
            ll_no_wifi_category.visibility = View.VISIBLE
        }

        addEvent()
    }

    private fun bindViewModel() {
        categoryViewModel.listCategories.observe(viewLifecycleOwner) {
            if (it != null) {
                categoryList.clear()
                categoryList.addAll(it)
                adapter?.notifyDataSetChanged()
            }
        }

        productViewModel.cartCount.observe(viewLifecycleOwner) { cartCount ->
            if (cartCount != null) {
                if (cartCount > 0 && tv_number_cart != null) {
                    tv_number_cart.visibility = View.VISIBLE
                    tv_number_cart.text = cartCount.toString()
                } else if (tv_number_cart != null) {
                    tv_number_cart.visibility = View.GONE
                }
            }
        }

    }
    private fun addEvent() {
        btn_try_connect.setOnClickListener {
            view?.let { it1 -> onViewCreated(it1, null) }
        }

        ll_cart_number.setOnClickListener {
            val user: FirebaseUser? = mAuth?.currentUser
            if (user != null) {
                val intentCart = Intent(context, CartActivity::class.java)
                startActivity(intentCart)
            } else {
                val intentCart = Intent(context, AuthActivity::class.java)
                startActivity(intentCart)
            }
        }

        gv_category.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, id -> // Get the GridView selected/clicked item text
                val selectedItem = parent.getItemAtPosition(position).toString()

                // Display the selected/clicked item text and position on TextView
                Toast.makeText(
                    requireContext(),
                    "GridView item clicked : ${categoryList[position].name} \\nAt index position : $position\"",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun inIt() {
        // create adapter
        adapter = CategoryAdapter(requireContext(), categoryList)
        gv_category.adapter = adapter
    }
}