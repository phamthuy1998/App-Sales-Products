package com.thuypham.ptithcm.mytiki.feature.employee.slide

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.observe
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.Slide
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentSlideDetailBinding
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.feature.employee.slide.adapter.CategorySpinnerAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.CategoryViewModel
import com.thuypham.ptithcm.mytiki.viewmodel.SlideViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SlideDetailFragment : BaseFragment<FragmentSlideDetailBinding>() {

    companion object {
        private const val REQUEST_CODE = 100
    }

    override val layoutId: Int = R.layout.fragment_slide_detail
    private val slideViewModel: SlideViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()

    private var imageUri: Uri? = null
    private var listCategory: ArrayList<Category> = arrayListOf()

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_option,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        slideViewModel.slide.value?.name ?: getString(R.string.btnAdd)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }

                    toolbar?.findViewById<ImageButton>(R.id.btnOption)?.apply {
                        if (slideViewModel.slide.value?.id != null) {
                            visibility = View.VISIBLE
                            setImageResource(R.drawable.ic_del)
                        } else visibility =  View.INVISIBLE
                        setOnClickListener { confirmDelItem() }
                    }

                }
            })
    }

    private fun confirmDelItem() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        with(builder)
        {
            setMessage(getString(R.string.dialogDelProduct))
            setPositiveButton(getString(R.string.dialogOk)) { dialog, _ ->
                slideViewModel.slide.value?.id?.let { slideViewModel.delSlide(it) }
                activity?.onBackPressed()
                dialog.dismiss()
            }
            setNegativeButton(getString(R.string.dialogCancel)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            viewBinding.ivSlide.setImageURI(data?.data)
            imageUri = data?.data
        }
    }

    private val categoryAdapter by lazy {
        CategorySpinnerAdapter()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun setEvents() {
        super.setEvents()
        viewBinding.btnSave.setOnClickListener {
            addSlide()
        }
        viewBinding.btnChooseImg.setOnClickListener {
            openGalleryForImage()
        }

        viewBinding.spCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    slideViewModel.category.value = listCategory[position]
                }

            }
    }

    private fun addSlide() {
        if (imageUri == null && viewBinding.slide?.image.isNullOrEmpty()) {
            Toast.makeText(activity, getString(R.string.errNoImage), Toast.LENGTH_LONG).show()
            return
        }

        val slideInput = Slide().apply {
            name = slideViewModel.name.value
            id = slideViewModel.slide.value?.id
            id_category = slideViewModel.category.value?.id
            name_category = slideViewModel.category.value?.name
            image = if (imageUri == null) viewBinding.slide?.image else ""
        }
        if (slideViewModel.slide.value?.equals(slideInput) == true&&imageUri==null) {
            Toast.makeText(activity, getString(R.string.nothingChange), Toast.LENGTH_LONG).show()
            return
        }
        slideViewModel.addSlide(slideInput, imageUri)
    }

    override fun initView() {
        super.initView()
        slideViewModel.slide.value = arguments?.get(Constant.SLIDE) as? Slide
        slideViewModel.name.value = slideViewModel.slide.value?.name

        categoryViewModel.getAllCategory()
        viewBinding.spCategory.adapter = categoryAdapter

        viewBinding.slide = slideViewModel.slide.value
        viewBinding.viewModel = slideViewModel
        viewBinding.isAdd = slideViewModel.slide.value?.id == null
    }

    override fun bindViewModel() {
        super.bindViewModel()
        categoryViewModel.listCategories.observe(viewLifecycleOwner) {
            if (it != null) {
                categoryAdapter.setCategoryList(it)
                listCategory = it
                val index = getCategoryItem(listCategory, slideViewModel.slide.value?.id_category)
                viewBinding.spCategory.setSelection(index)
            }
        }

        slideViewModel.slideAdd.observe(viewLifecycleOwner) {
            viewBinding.slide = it
            slideViewModel.slide.value = it
        }

        slideViewModel.networkAddSlide.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.RUNNING -> {
                    viewBinding.progressSlide.visible()
                    viewBinding.btnSave.isEnabled = false
                }
                Status.SUCCESS -> {
                    viewBinding.progressSlide.gone()
                    viewBinding.btnSave.isEnabled = true
                    viewBinding.isAdd = false
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.addSuccess),
                        Toast.LENGTH_LONG
                    ).show()
                }
                Status.LOADING_PROCESS -> {
                    viewBinding.progressSlide.gone()
                    viewBinding.btnSave.isEnabled = true
                }
                Status.FAILED -> {
                    Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_LONG).show()
                    viewBinding.progressSlide.gone()
                    viewBinding.btnSave.isEnabled = true
                }
            }
        }
    }

    private fun getCategoryItem(
        listCategory: java.util.ArrayList<Category>,
        idCategory: String?
    ): Int {
        for (pos in 0 until listCategory.size step 1) {
            if (listCategory[pos].id == idCategory) {
                slideViewModel.category.value = listCategory[pos]
                return pos
            }
        }
        return 0
    }

}
