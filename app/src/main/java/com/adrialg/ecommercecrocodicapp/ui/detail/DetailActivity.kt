package com.adrialg.ecommercecrocodicapp.ui.detail

import android.os.Bundle
import com.adrialg.ecommercecrocodicapp.R
import com.adrialg.ecommercecrocodicapp.base.BaseActivity
import com.adrialg.ecommercecrocodicapp.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding, DetailViewModel>(R.layout.activity_detail) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = resources.getColor(R.color.white)

    }
}