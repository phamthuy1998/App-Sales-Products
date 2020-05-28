package com.thuypham.ptithcm.mytiki.feature.admin.acc

import android.app.AlertDialog
import android.os.Build
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.observe
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.BaseActivity
import com.thuypham.ptithcm.mytiki.base.BaseFragment
import com.thuypham.ptithcm.mytiki.builder.toolbarFunctionQueue
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.databinding.FragmentAccountDetailBinding
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.setupToolbar
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.util.DatePickerFragment
import com.thuypham.ptithcm.mytiki.viewmodel.AccountViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AccountDetailFragment : BaseFragment<FragmentAccountDetailBinding>() {

    override val layoutId: Int = R.layout.fragment_account_detail

    private val accViewModel: AccountViewModel by viewModel()
    private lateinit var arrRole: Array<String>

    override fun setUpToolbar() {
        super.setUpToolbar()
        (activity as? BaseActivity<*>)?.setupToolbar(
            toolbarLayoutId = R.layout.toolbar_option,
            rootViewId = (activity as? BaseActivity<*>)?.toolbarViewParentId, hasBack = false,
            messageQueue = toolbarFunctionQueue {
                func { curActivity, toolbar ->
                    toolbar?.findViewById<TextView>(R.id.tvTitleToolbar)?.text =
                        accViewModel.user.value?.name ?: getString(R.string.btnAdd)
                    curActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
                    toolbar?.findViewById<ImageButton>(R.id.icBack)?.apply {
                        setOnClickListener { activity?.onBackPressed() }
                    }

                    toolbar?.findViewById<ImageButton>(R.id.btnOption)?.apply {
                        if (accViewModel.user.value?.id != null) {
                            visibility = View.VISIBLE
                            setImageResource(R.drawable.ic_del)
                        } else visibility = View.INVISIBLE
                        setOnClickListener { confirmDelAcc() }
                    }
                }
            })
    }

    private fun confirmDelAcc() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        with(builder)
        {
            setMessage(getString(R.string.dialogDelProduct))
            setPositiveButton(getString(R.string.dialogOk)) { dialog, _ ->
                accViewModel.user.value?.let { accViewModel.delAccount(it) }
                activity?.onBackPressed()
                dialog.dismiss()
            }
            setNegativeButton(getString(R.string.dialogCancel)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    // Show calendar to select birthday
    fun showCalendar() {
        DatePickerFragment().show(
            requireActivity().supportFragmentManager,
            "Choose a date of birth"
        )
    }

    override fun setEvents() {
        super.setEvents()
        viewBinding.spRole.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                accViewModel.user.value?.role = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    override fun initView() {
        super.initView()
        arrRole = resources.getStringArray(R.array.role)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrRole)
        viewBinding.spRole.adapter = adapter

        accViewModel.user.value = arguments?.get(Constant.USER) as? User ?: User()
        accViewModel.user.value?.let { accViewModel.setUser(it) }
        viewBinding.viewModel = accViewModel
        viewBinding.fragment = this
        if (accViewModel.user.value?.email == null) {
            viewBinding.isAdd = true
            viewBinding.spRole.setSelection(1)
        } else {
            viewBinding.isAdd = false
            accViewModel.user.value?.role?.minus(1)?.let { viewBinding.spRole.setSelection(it) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAcc() {
        accViewModel.setUserUpdate()
        if (viewBinding.isAdd == true) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val dayCreate = current.format(formatter)

            accViewModel.user.value?.let {
                it.daycreate = dayCreate
                accViewModel.createAcc(it)
            }
        } else accViewModel.user.value?.let { accViewModel.updateAcc(it) }
    }

    override fun bindViewModel() {
        super.bindViewModel()

        accViewModel.userCreated.observe(viewLifecycleOwner) {

        }
        accViewModel.userUpdated.observe(viewLifecycleOwner) {

        }
        accViewModel.networkUpdateAcc.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.RUNNING -> {
                    viewBinding.progressAcc.visible()
                    viewBinding.btnAddAcc.isEnabled = false
                }
                Status.SUCCESS -> {
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                    viewBinding.isAdd = false
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.addSuccess),
                        Toast.LENGTH_LONG
                    ).show()
                }
                Status.LOADING_PROCESS -> {
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                }
                Status.FAILED -> {
                    Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_LONG).show()
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                }
            }

        }

        accViewModel.networkCreateAcc.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.RUNNING -> {
                    viewBinding.progressAcc.visible()
                    viewBinding.btnAddAcc.isEnabled = false
                }
                Status.SUCCESS -> {
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                    viewBinding.isAdd = false
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.addSuccess),
                        Toast.LENGTH_LONG
                    ).show()
                }
                Status.LOADING_PROCESS -> {
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                }
                Status.FAILED -> {
                    Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_LONG).show()
                    viewBinding.progressAcc.gone()
                    viewBinding.btnAddAcc.isEnabled = true
                }
            }
        }
    }

}
