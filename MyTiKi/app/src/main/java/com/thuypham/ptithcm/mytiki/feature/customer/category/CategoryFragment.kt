package com.thuypham.ptithcm.mytiki.feature.customer.category

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.feature.customer.cart.CartActivity
import com.thuypham.ptithcm.mytiki.feature.customer.category.adapter.CategoryAdapter
import com.thuypham.ptithcm.mytiki.viewmodel.CategoryViewModel
import com.thuypham.ptithcm.mytiki.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.category_fragment.*
import kotlinx.android.synthetic.main.ll_cart.*
import kotlinx.android.synthetic.main.no_wifi.*
import org.jetbrains.anko.support.v4.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class CategoryFragment : Fragment() {

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
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
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
                } else if (tv_number_cart != null) tv_number_cart.visibility = View.GONE
            }
        }
    }

    private fun addEvent() {
        btn_try_connect.setOnClickListener { view?.let { it1 -> onViewCreated(it1, null) } }
        ll_cart_number.setOnClickListener { startActivity<CartActivity>() }
    }

    private fun inIt() {
        adapter = CategoryAdapter(requireContext(), categoryList)
        gv_category.adapter = adapter
    }
}