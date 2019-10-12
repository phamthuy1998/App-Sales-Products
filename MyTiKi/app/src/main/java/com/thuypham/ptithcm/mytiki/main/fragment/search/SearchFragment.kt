package com.thuypham.ptithcm.mytiki.main.fragment.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.adapters.SearchViewBindingAdapter.setOnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.thuypham.ptithcm.mytiki.R
import kotlinx.android.synthetic.main.search_fragment.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.main.product.activity.FavoriteActivity
import com.thuypham.ptithcm.mytiki.main.product.adapter.ProductDetailApdater
import com.thuypham.ptithcm.mytiki.main.product.model.Product
import kotlinx.android.synthetic.main.activity_favorite.*
import java.util.Collections.replaceAll
import java.text.Normalizer
import java.util.regex.Pattern


class SearchFragment : Fragment() {

    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var listProductSearch = ArrayList<Product>()
    private var productSearchAdapter: ProductDetailApdater? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        // product viewed init
        productSearchAdapter =
            ProductDetailApdater(listProductSearch, requireContext())
        // Set rcyclerview horizontal
        rv_search.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        rv_search.adapter = productSearchAdapter
        addEvent()
    }

    private fun addEvent() {
        btn_cancel_search.setOnClickListener {
            activity?.finish()
        }

        sv_product.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(search: String): Boolean {
                if (!search.trim().isEmpty())
                    getListProductSearch(search)
                else
                    listProductSearch.clear()
                return false
            }

            override fun onQueryTextChange(search: String): Boolean {
                if (!search.trim().isEmpty())
                    getListProductSearch(search)
                else
                    listProductSearch.clear()
                return false
            }
        })
    }

    @SuppressLint("DefaultLocale")
    fun removeAccent(s: String): String {
        val str = s.toLowerCase()
        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("")
    }

    private fun getListProductSearch(keySearch: String) {
        val search = removeAccent(keySearch)
        if (!keySearch.isEmpty()) {
            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.PRODUCT)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        listProductSearch.clear()
                        for (ds in dataSnapshot.children) {
                            val id = ds.child(PhysicsConstants.PRODUCT_ID).value as String
                            val name = ds.child(PhysicsConstants.NAME_PRODUCT).value as String
                            val price = ds.child(PhysicsConstants.PRICE_PRODUCT).value as Long
                            val image = ds.child(PhysicsConstants.IMAGE_PRODUCT).value as String
                            val infor = ds.child(PhysicsConstants.INFOR_PRODUCT).value as String
                            val product_count =
                                ds.child(PhysicsConstants.PRODUCT_COUNT).value as Long
                            val id_category =
                                ds.child(PhysicsConstants.ID_CATEGORY_PRODUCT).value as String
                            val sale = ds.child(PhysicsConstants.PRODUCT_SALE).value as Long

                            //Chuyển qua tiếng việt không dáu để tìm kiếm
                            if (removeAccent(name).contains(search)
                                || id.contains(search)
                                || price.toString().contains(search)
                                || image.contains(search)
                                || removeAccent(infor).contains(search)
                                || id_category.contains(search)
                            ) {
                                listProductSearch.add(
                                    Product(
                                        id,
                                        name,
                                        price,
                                        image,
                                        infor,
                                        product_count,
                                        id_category,
                                        sale
                                    )
                                )
                            }
                        }
                        if (!listProductSearch.isEmpty()) {
                            ll_search_empty.visibility = View.GONE
                            productSearchAdapter?.notifyDataSetChanged()
                        } else {
                            ll_search_empty.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
        }
    }

}
