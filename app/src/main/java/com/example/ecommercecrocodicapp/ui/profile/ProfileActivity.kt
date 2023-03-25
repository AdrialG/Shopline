package com.example.ecommercecrocodicapp.ui.profile

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.example.ecommercecrocodicapp.R
import com.example.ecommercecrocodicapp.base.BaseActivity
import com.example.ecommercecrocodicapp.data.Session
import com.example.ecommercecrocodicapp.databinding.ActivityProfileBinding
import com.example.ecommercecrocodicapp.ui.edit.EditActivity
import com.example.ecommercecrocodicapp.ui.home.HomeActivity
import com.example.ecommercecrocodicapp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

    @Inject
    lateinit var session: Session
    private var filePhoto: File? = null
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = resources.getColor(R.color.white)

        binding.profileBack.setOnClickListener {
            openActivity<HomeActivity>()
        }

        binding.updateButton.setOnClickListener {
            openActivity<EditActivity> {  }
        }

        binding.logOut.setOnClickListener {
            logout()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                //Use When Channel
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                val user = session.getUser()
                                binding.user = user
                            }
                            else -> {}
                        }
                    }
                }
                launch {
                    getUser()
                }
            }
        }
    }

    private fun getUser() {
        val user = session.getUser()
        binding.user = user
        viewModel.getProfile()
    }

    private fun logout() {
        binding.logOut.setOnClickListener {
            viewModel.logout()
            openActivity<LoginActivity>()
            finishAffinity()
        }
    }

}

