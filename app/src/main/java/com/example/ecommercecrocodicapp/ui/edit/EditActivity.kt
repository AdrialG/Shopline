package com.example.ecommercecrocodicapp.ui.edit

import android.os.Bundle
import com.example.ecommercecrocodicapp.R
import com.example.ecommercecrocodicapp.base.BaseActivity
import com.example.ecommercecrocodicapp.databinding.ActivityEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditActivity : BaseActivity<ActivityEditBinding, EditViewModel>(R.layout.activity_edit) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = resources.getColor(R.color.white)

    }
}