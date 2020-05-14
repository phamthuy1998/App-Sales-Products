package com.thuypham.ptithcm.mytiki.feature.employee.category

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.databinding.FragmentCategoryDetailBinding
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.CategoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class CategoryDetailFragment : BaseFragment<FragmentCategoryDetailBinding>() {
    companion object {
        private const val REQUEST_CODE = 100
    }

    override val layoutId: Int = R.layout.fragment_category_detail

    private val categoryViewModel: CategoryViewModel by viewModel()

    private var imageUri: Uri? = null

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_option,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        categoryViewModel.category.value?.name ?: getString(R.string.btnAdd)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }

                    toolbar?.findViewById<ImageButton>(R.id.btnOption)?.apply {
                        if (categoryViewModel.category.value?.id != null) {
                            visibility = View.VISIBLE
                            setImageResource(R.drawable.ic_del)
                        } else visibility = View.INVISIBLE
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
                categoryViewModel.category.value?.id?.let { categoryViewModel.delCategory(it) }
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
            viewBinding.ivcategory.setImageURI(data?.data)
            imageUri = data?.data
        }
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
    }

    private fun addSlide() {
        if (imageUri == null && categoryViewModel.category.value?.image.isNullOrEmpty()) {
            Toast.makeText(activity, getString(R.string.errNoImage), Toast.LENGTH_LONG).show()
            return
        }

        val category = Category().apply {
            name = categoryViewModel.name.value
            id = categoryViewModel.category.value?.id
            image = categoryViewModel.category.value?.image ?: ""
        }
        if (categoryViewModel.category.value?.equals(category) == true) {
            Toast.makeText(activity, getString(R.string.nothingChange), Toast.LENGTH_LONG).show()
            return
        }
        categoryViewModel.addCategory(category, imageUri)
    }

    override fun initView() {
        super.initView()
        categoryViewModel.category.value = arguments?.get(Constant.CATEGORY) as? Category
        categoryViewModel.name.value = categoryViewModel.category.value?.name

        viewBinding.category = categoryViewModel.category.value
        viewBinding.viewModel = categoryViewModel
        viewBinding.isAdd = categoryViewModel.category.value?.id == null
    }

    override fun bindViewModel() {
        super.bindViewModel()

        categoryViewModel.categoryAdd.observe(viewLifecycleOwner) {
            viewBinding.category = it
            categoryViewModel.category.value = it
        }

        categoryViewModel.networkAddCategory.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.RUNNING -> {
                    viewBinding.progressCategory.visible()
                    viewBinding.btnSave.isEnabled = false
                }
                Status.SUCCESS -> {
                    viewBinding.progressCategory.gone()
                    viewBinding.btnSave.isEnabled = true
                    viewBinding.isAdd = false
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.addSuccess),
                        Toast.LENGTH_LONG
                    ).show()
                }
                Status.LOADING_PROCESS -> {
                    viewBinding.progressCategory.gone()
                    viewBinding.btnSave.isEnabled = true
                }
                Status.FAILED -> {
                    Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_LONG).show()
                    viewBinding.progressCategory.gone()
                    viewBinding.btnSave.isEnabled = true
                }
            }
        }
    }

}
