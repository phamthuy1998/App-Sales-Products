package com.thuypham.ptithcm.mytiki.feature.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.thuypham.ptithcm.mytiki.databinding.FragmentSignInBinding
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SignInFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null


    val userViewModel: UserViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSignInBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this@SignInFragment
        binding.userViewModel = userViewModel

        return binding.root
    }
}
