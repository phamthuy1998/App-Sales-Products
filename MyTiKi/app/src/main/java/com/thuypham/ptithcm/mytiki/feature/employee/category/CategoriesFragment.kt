package com.thuypham.ptithcm.mytiki.feature.employee.category

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentCategoriesBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
import com.thuypham.ptithcm.mytiki.feature.employee.category.adapter.CategoryEmployeeAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.CategoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class CategoriesFragment : BaseFragment<FragmentCategoriesBinding>() {

    override val layoutId: Int = R.layout.fragment_categories
    private val categoryViewModel: CategoryViewModel by viewModel()
    private val categoryAdapter by lazy {
        CategoryEmployeeAdapter{ categoryID -> showCategoryDetail(categoryID) }
    }

    private fun showCategoryDetail(categoryID: String) {
        val bundle = bundleOf(Constant.CATEGORY_ID to categoryID)
        findNavController().navigate(R.id.categoryDetailFragment, bundle)
    }

    override fun initView() {
        super.initView()
        categoryViewModel.getAllCategory()
        viewBinding.rvCategories.adapter = categoryAdapter
        viewBinding.rvCategories.setHasFixedSize(true)
        viewBinding.rvCategories.setItemViewCacheSize(20)
    }

    override fun bindViewModel() {
        super.bindViewModel()
        categoryViewModel.listCategories.observe(viewLifecycleOwner, Observer {
            categoryAdapter.setCategoryList(it)
        })
        categoryViewModel.networkListCategory.observe(viewLifecycleOwner, Observer {
            viewBinding.progressCategory.visibility =
                if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        })
    }
}
