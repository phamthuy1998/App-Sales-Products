package com.thuypham.ptithcm.mytiki.feature.customer.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.feature.customer.product.adapter.ProductDetailAdapter
import com.thuypham.ptithcm.mytiki.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {


    private val productSearchAdapter: ProductDetailAdapter by lazy {
        ProductDetailAdapter()
    }

    private val productViewModel: ProductViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_search.adapter = productSearchAdapter
        addEvent()
        bindViewModel()
    }

    private fun bindViewModel() {
        productViewModel.listProductSearch.observe(viewLifecycleOwner) {
            productSearchAdapter.setData(it)
        }
    }

    private fun addEvent() {
        sv_product.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(search: String): Boolean {
                if (search.trim().isNotEmpty()) productViewModel.getProductSearch(search)
                else productSearchAdapter.removeAllData()
                return false
            }

            override fun onQueryTextChange(search: String): Boolean {
                if (search.trim().isNotEmpty()) productViewModel.getProductSearch(search)
                else productSearchAdapter.removeAllData()
                return false
            }
        })
    }
}
