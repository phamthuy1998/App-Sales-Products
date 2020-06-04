package com.thuypham.ptithcm.mytiki.feature.employee.product

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.observe
import com.sg.vivastory.ext.getTxtTrim
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.*
import com.thuypham.ptithcm.mytiki.databinding.FragmentProductDetailBinding
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.feature.employee.slide.adapter.CategorySpinnerAdapter
import com.thuypham.ptithcm.mytiki.util.Constant.PRODUCT
import com.thuypham.ptithcm.mytiki.viewmodel.CategoryViewModel
import com.thuypham.ptithcm.mytiki.viewmodel.ProductViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>() {

    companion object {
        private const val REQUEST_CODE = 100
    }

    private val productViewModel: ProductViewModel by viewModel()

    private var imageUri: Uri? = null
    private var listCategory: ArrayList<Category> = arrayListOf()
    private val categoryViewModel: CategoryViewModel by viewModel()
    override val layoutId: Int = R.layout.fragment_product_detail


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            viewBinding.ivProduct.setImageURI(data?.data)
            imageUri = data?.data
        }
    }

    private val categoryAdapter by lazy {
        CategorySpinnerAdapter()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel.product.value = arguments?.get(PRODUCT) as? Product
    }

    override fun initView() {
        super.initView()

        categoryViewModel.getAllCategory()
        viewBinding.spCategory.adapter = categoryAdapter

        viewBinding.product = productViewModel.product.value
        viewBinding.viewModel = productViewModel
        viewBinding.isAdd = productViewModel.product.value?.id == null
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun setEvents() {
        super.setEvents()
        viewBinding.btnSave.setOnClickListener {
            addProduct()
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
                    productViewModel.category.value = listCategory[position]
                }

            }
    }

    private fun addProduct() {
        var check = true
        if (imageUri == null && productViewModel.product.value?.image.isNullOrEmpty()) {
            Toast.makeText(activity, getString(R.string.errNoImage), Toast.LENGTH_LONG).show()
            check = false
        }
        val nameInput: String? = viewBinding.edtProductName.getTxtTrim()
        val priceInput: String? = viewBinding.edtPrice.getTxtTrim()
        val productCountInput: String? = viewBinding.edtProductCount.getTxtTrim()
        val infoInput: String? = viewBinding.edtInfo.getTxtTrim()
        val saleInput: String? = viewBinding.edtSale.getTxtTrim()
        val idCategoryInput = productViewModel.category.value?.id
        if (nameInput == "") {
            check = false
            viewBinding.edtProductName.error = getString(R.string.err_product_name)
        }
        if (priceInput == "") {
            check = false
            viewBinding.edtPrice.error = getString(R.string.errPrice)
        }
        if (productCountInput == "") {
            check = false
            viewBinding.edtProductCount.error = getString(R.string.errProCount)
        }
        if (infoInput == "") {
            check = false
            viewBinding.edtInfo.error = getString(R.string.errProInfo)
        }
        if (check) {
            val product = Product().apply {
                id = productViewModel.product.value?.id
                name = nameInput
                price = priceInput?.toLong() ?: 0
                image = productViewModel.product.value?.image
                product_count = productCountInput?.toLong() ?: 0
                infor = infoInput
                sale = saleInput?.toLong() ?: 0
                id_category = idCategoryInput
            }

            if (productViewModel.product.value?.equals(product) == true && imageUri == null) {
                Toast.makeText(activity, getString(R.string.nothingChange), Toast.LENGTH_LONG)
                    .show()
                return
            }

            productViewModel.addProduct(product, imageUri)
        }

    }

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_option,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        productViewModel.product.value?.name ?: getString(R.string.btnAdd)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }

                    toolbar?.findViewById<ImageButton>(R.id.btnOption)?.apply {
                        if (productViewModel.product.value?.id != null) {
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
            setMessage(getString(R.string.dialogDelSlide))
            setPositiveButton(getString(R.string.dialogOk)) { dialog, _ ->
                productViewModel.product.value?.id?.let { productViewModel.delProduct(it) }
                activity?.onBackPressed()
                dialog.dismiss()
            }
            setNegativeButton(getString(R.string.dialogCancel)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun getCategoryItem(
        listCategory: java.util.ArrayList<Category>,
        idCategory: String?
    ): Int {
        for (pos in 0 until listCategory.size step 1) {
            if (listCategory[pos].id == idCategory) {
                productViewModel.category.value = listCategory[pos]
                return pos
            }
        }
        return 0
    }

    override fun bindViewModel() {
        super.bindViewModel()
        categoryViewModel.listCategories.observe(viewLifecycleOwner) {
            if (it != null) {
                categoryAdapter.setCategoryList(it)
                listCategory = it
                val index =
                    getCategoryItem(listCategory, productViewModel.product.value?.id_category)
                viewBinding.spCategory.setSelection(index)
            }
        }

        productViewModel.productAdd.observe(viewLifecycleOwner) {
            productViewModel.product.value = it
            sendNotification(it)
        }

        productViewModel.networkAddProduct.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.RUNNING -> {
                    viewBinding.progressProduct.visible()
                    viewBinding.btnSave.isEnabled = false
                }
                Status.SUCCESS -> {
                    viewBinding.progressProduct.gone()
                    viewBinding.btnSave.isEnabled = true
                    viewBinding.isAdd = false
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.addSuccess),
                        Toast.LENGTH_LONG
                    ).show()
                }
                Status.LOADING_PROCESS -> {
                    viewBinding.progressProduct.gone()
                    viewBinding.btnSave.isEnabled = true
                }
                Status.FAILED -> {
                    Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_LONG).show()
                    viewBinding.progressProduct.gone()
                    viewBinding.btnSave.isEnabled = true
                }
            }
        }
    }

    private fun sendNotification(product: Product) {
        val sendNotification = ProductAdd().apply {
            data = Data().apply {
                idProduct = product.id
                name = product.name
                image = product.image
                title = "New product"
            }
            to = "/topics/newProduct"
        }
        productViewModel.sendNotificationAddNewProduct(sendNotification)
    }

}
