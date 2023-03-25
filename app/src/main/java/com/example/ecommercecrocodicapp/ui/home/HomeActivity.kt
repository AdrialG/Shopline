package com.example.ecommercecrocodicapp.ui.home

import android.os.Bundle
import com.crocodic.core.extension.openActivity
import com.example.ecommercecrocodicapp.R
import com.example.ecommercecrocodicapp.base.BaseActivity
import com.example.ecommercecrocodicapp.data.Session
import com.example.ecommercecrocodicapp.databinding.ActivityHomeBinding
import com.example.ecommercecrocodicapp.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    @Inject
    lateinit var session: Session

    override fun onStart() {
        super.onStart()
        viewModel.getProfile()
        val user = session.getUser()
        binding.user = user
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.light_grey)
        val user = session.getUser()

        getProductList()

        binding.profilePictureHome.setOnClickListener {
            openActivity<ProfileActivity>()
        }

    }

    private fun getProductList() {
        viewModel.productList()
    }

}