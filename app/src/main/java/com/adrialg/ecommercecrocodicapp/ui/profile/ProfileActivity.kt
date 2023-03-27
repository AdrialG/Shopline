package com.adrialg.ecommercecrocodicapp.ui.profile

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.adrialg.ecommercecrocodicapp.R
import com.adrialg.ecommercecrocodicapp.base.BaseActivity
import com.adrialg.ecommercecrocodicapp.data.Session
import com.adrialg.ecommercecrocodicapp.databinding.ActivityProfileBinding
import com.adrialg.ecommercecrocodicapp.ui.edit.EditActivity
import com.adrialg.ecommercecrocodicapp.ui.home.HomeActivity
import com.adrialg.ecommercecrocodicapp.ui.login.LoginActivity
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

