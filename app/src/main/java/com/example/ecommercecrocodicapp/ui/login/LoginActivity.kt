package com.example.ecommercecrocodicapp.ui.login

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.example.ecommercecrocodicapp.R
import com.example.ecommercecrocodicapp.base.BaseActivity
import com.example.ecommercecrocodicapp.databinding.ActivityLoginBinding
import com.example.ecommercecrocodicapp.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.signInButton.setOnClickListener {
            val phone = binding.inputPhone.textOf()
            val password = binding.inputPassword.textOf()
            viewModel.login(phone, password)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show(getString(R.string.logging_in))
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<HomeActivity>()
                                finish()
                            }

                            //bro please
                            ApiStatus.ERROR -> {
                                loadingDialog.dismiss()
                                tos(it.message ?: "Login Failed")
                            }
                            else -> loadingDialog.setResponse("This is the message of all time")
                        }
                    }
                }
            }
        }
    }
}