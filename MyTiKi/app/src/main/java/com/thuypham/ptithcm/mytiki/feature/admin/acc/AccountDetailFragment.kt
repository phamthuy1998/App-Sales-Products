package com.thuypham.ptithcm.mytiki.feature.admin.acc

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.databinding.FragmentAccountDetailBinding
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.AccountViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class AccountDetailFragment : BaseFragment<FragmentAccountDetailBinding>() {

    override val layoutId: Int = R.layout.fragment_account_detail

    private val accViewModel: AccountViewModel by viewModel()

    private var imageUri: Uri? = null

//    override fun setUpToolbar() {
//        super.setUpToolbar()
//        (activity as? BaseActivity<*>)?.setupToolbar(
//            toolbarLayoutId = R.layout.toolbar_option,
//            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
//            messageQueue = toolbarFunctionQueue {
//                func { curActivity, toolbar ->
//                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
//                        accViewModel.category.value?.name ?: getString(R.string.btnAdd)
//                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
//                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
//                        setOnClickListener { activity?.onBackPressed() }
//                    }
//
//                    toolbar?.findViewById<ImageButton>(R.id.btnOption)?.apply {
//                        if (accViewModel.category.value?.id != null) {
//                            visibility = View.VISIBLE
//                            setImageResource(R.drawable.ic_del)
//                        } else visibility = View.INVISIBLE
//                        setOnClickListener { confirmDelAcc() }
//                    }
//
//                }
//            })
//    }
//
//    private fun confirmDelAcc() {
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setCancelable(false)
//        with(builder)
//        {
//            setMessage(getString(R.string.dialogDelProduct))
//            setPositiveButton(getString(R.string.dialogOk)) { dialog, _ ->
//                accViewModel.user.value?.let { accViewModel.delAccount(it) }
//                activity?.onBackPressed()
//                dialog.dismiss()
//            }
//            setNegativeButton(getString(R.string.dialogCancel)) { dialog, _ ->
//                dialog.dismiss()
//            }
//            show()
//        }
//    }
//
//
//    override fun setEvents() {
//        super.setEvents()
//        viewBinding.btnSave.setOnClickListener {
//            addSlide()
//        }
//
//    }
//
//    private fun addSlide() {
//        if (imageUri == null && accViewModel.category.value?.image.isNullOrEmpty()) {
//            Toast.makeText(activity, getString(R.string.errNoImage), Toast.LENGTH_LONG).show()
//            return
//        }
//
//        val category = Category().apply {
//            name = accViewModel.name.value
//            id = accViewModel.category.value?.id
//            image = accViewModel.category.value?.image ?: ""
//        }
//        if (accViewModel.category.value?.equals(category) == true) {
//            Toast.makeText(activity, getString(R.string.nothingChange), Toast.LENGTH_LONG).show()
//            return
//        }
//        accViewModel.addCategory(category, imageUri)
//    }
//
//    override fun initView() {
//        super.initView()
//        accViewModel.user.value = arguments?.get(Constant.USER) as? User
//        accViewModel.name.value = accViewModel.category.value?.name
//
//        viewBinding.category = accViewModel.category.value
//        viewBinding.viewModel = accViewModel
//        viewBinding.isAdd = accViewModel.category.value?.id == null
//    }
//
//    override fun bindViewModel() {
//        super.bindViewModel()
//
//        accViewModel.categoryAdd.observe(viewLifecycleOwner) {
//            viewBinding.category = it
//            accViewModel.category.value = it
//        }
//
//        accViewModel.networkAddCategory.observe(viewLifecycleOwner) {
//            when (it.status) {
//                Status.RUNNING -> {
//                    viewBinding.progressCategory.visible()
//                    viewBinding.btnSave.isEnabled = false
//                }
//                Status.SUCCESS -> {
//                    viewBinding.progressCategory.gone()
//                    viewBinding.btnSave.isEnabled = true
//                    viewBinding.isAdd = false
//                    Toast.makeText(
//                        requireActivity(),
//                        getString(R.string.addSuccess),
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//                Status.LOADING_PROCESS -> {
//                    viewBinding.progressCategory.gone()
//                    viewBinding.btnSave.isEnabled = true
//                }
//                Status.FAILED -> {
//                    Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_LONG).show()
//                    viewBinding.progressCategory.gone()
//                    viewBinding.btnSave.isEnabled = true
//                }
//            }
//        }
//    }

}
