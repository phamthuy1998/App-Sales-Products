package com.thuypham.ptithcm.mytiki.feature.employee.slide

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentSlidesBinding
import com.thuypham.ptithcm.mytiki.ext.findNavController
import com.thuypham.ptithcm.mytiki.feature.employee.slide.adapter.SlideAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.SlideViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SlidesFragment : BaseFragment<FragmentSlidesBinding>() {

    override val layoutId: Int = R.layout.fragment_slides
    private val slideViewModel: SlideViewModel by viewModel()
    private val slideAdapter by lazy {
        SlideAdapter { slideID -> showSlideDetail(slideID) }
    }

    private fun showSlideDetail(slideID: String) {
        val bundle = bundleOf(Constant.ORDER_ID to slideID)
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
            slideAdapter.setSlideList(it)
        })

        slideViewModel.networkListSlide.observe(viewLifecycleOwner, Observer {
            viewBinding.progressSlides.visibility =
                if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        })
    }

}