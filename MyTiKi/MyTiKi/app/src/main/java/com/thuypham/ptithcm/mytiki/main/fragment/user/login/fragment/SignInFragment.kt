package com.thuypham.ptithcm.mytiki.main.fragment.user.login.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth

import com.thuypham.ptithcm.mytiki.databinding.FragmentSignInBinding
import com.thuypham.ptithcm.mytiki.main.fragment.user.viewmodel.UserViewModel


class SignInFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null


    val userViewModel: UserViewModel by lazy {
        ViewModelProviders
                .of(activity!!)
                .get(UserViewModel::class.java)
    }

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
