package com.thuypham.ptithcm.mytiki.feature.employee.category

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentCategoriesBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
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
        CategoryEmployeeAdapter { category -> showCategoryDetail(category) }
    }


    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_option,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        getString(R.string.categories)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }
                    toolbar?.findViewById<ImageButton>(R.id.btnOption)?.apply {
                        setOnClickListener { showCategoryDetail(null) }
                    }
                }
            })
    }

    private fun showCategoryDetail(category: Category?) {
        val bundle = bundleOf(Constant.CATEGORY to category)
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
            it?.reverse()
            categoryAdapter.setCategoryList(it)
        })
        categoryViewModel.networkListCategory.observe(viewLifecycleOwner, Observer {
            viewBinding.progressCategory.visibility =
                if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        })
    }
}
