package com.example.ecommercecrocodicapp.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.crocodic.core.extension.openActivity
import com.example.ecommercecrocodicapp.R
import com.example.ecommercecrocodicapp.base.BaseActivity
import com.example.ecommercecrocodicapp.databinding.ActivityMainBinding
import com.example.ecommercecrocodicapp.ui.login.LoginActivity
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