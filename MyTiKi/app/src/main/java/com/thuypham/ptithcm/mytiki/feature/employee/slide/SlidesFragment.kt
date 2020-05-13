package com.thuypham.ptithcm.mytiki.feature.employee.slide

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Slide
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentSlidesBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.feature.employee.slide.adapter.SlideAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.SlideViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SlidesFragment : BaseFragment<FragmentSlidesBinding>() {

    override val layoutId: Int = R.layout.fragment_slides
    private val slideViewModel: SlideViewModel by viewModel()
    private val slideAdapter by lazy {
        SlideAdapter { slide -> showSlideDetail(slide) }
    }

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_option,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        getString(R.string.slide)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }
                    toolbar?. findViewById < ImageButton >(R.id.btnOption)?.apply {
                        setOnClickListener {  showSlideDetail(null)}
                    }
                }
            })
    }


    private fun showSlideDetail(slide: Slide?) {
        val bundle = bundleOf(Constant.SLIDE to slide)
        findNavController().navigate(R.id.slideDetailFragment, bundle)
    }

    override fun initView() {
        super.initView()
        slideViewModel.getAllSlide()
        viewBinding.rvSlides.adapter = slideAdapter
        viewBinding.rvSlides.setHasFixedSize(true)
        viewBinding.rvSlides.setItemViewCacheSize(20)
    }

    override fun setEvents() {
        super.setEvents()
        viewBinding.swRefreshSlide.setOnRefreshListener { refreshSlide() }
    }

    private fun refreshSlide() {
        viewBinding.swRefreshSlide.isRefreshing = false
    }

    override fun bindViewModel() {
        super.bindViewModel()
        slideViewModel.listSlide.observe(viewLifecycleOwner, Observer {
            it.reverse()
            slideAdapter.setSlideList(it)
        })

        slideViewModel.networkListSlide.observe(viewLifecycleOwner, Observer {
            viewBinding.progressSlides.visibility =
                if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        })
    }

}