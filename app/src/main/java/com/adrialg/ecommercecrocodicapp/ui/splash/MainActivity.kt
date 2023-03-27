package com.adrialg.ecommercecrocodicapp.ui.splash

import android.os.Bundle
import com.crocodic.core.extension.openActivity
import com.adrialg.ecommercecrocodicapp.R
import com.adrialg.ecommercecrocodicapp.base.BaseActivity
import com.adrialg.ecommercecrocodicapp.databinding.ActivityMainBinding
import com.adrialg.ecommercecrocodicapp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = resources.getColor(R.color.white)

        viewModel.splash {
            openActivity<LoginActivity>()
            finish()
        }
    }
}