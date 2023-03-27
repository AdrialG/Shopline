package com.adrialg.ecommercecrocodicapp.ui.home

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.adrialg.ecommercecrocodicapp.R
import com.adrialg.ecommercecrocodicapp.base.BaseActivity
import com.adrialg.ecommercecrocodicapp.data.Const
import com.adrialg.ecommercecrocodicapp.data.Product
import com.adrialg.ecommercecrocodicapp.data.Session
import com.adrialg.ecommercecrocodicapp.databinding.ActivityHomeBinding
import com.adrialg.ecommercecrocodicapp.databinding.HomeItemLayoutBinding
import com.adrialg.ecommercecrocodicapp.ui.detail.DetailActivity
import com.adrialg.ecommercecrocodicapp.ui.profile.ProfileActivity
import com.adrialg.ecommercecrocodicapp.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    @Inject
    lateinit var session: Session

    private val adapter by lazy {
        ReactiveListAdapter<HomeItemLayoutBinding, Product>(R.layout.home_item_layout).initItem { position, data ->
            openActivity<DetailActivity> {
                putExtra(Const.PRODUCT.ID, data)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = resources.getColor(R.color.white)

        observe()
        getUser()
        getProduct()

        binding.recyclerUnggul.adapter = adapter
        binding.recyclerAll.adapter = adapter

        viewModel.productList()

        binding.searchHome.setOnClickListener {
            openActivity<SearchActivity>()
        }

        binding.searchText.setOnClickListener {
            openActivity<SearchActivity>()
        }

        binding.profilePictureHome.setOnClickListener {
            openActivity<ProfileActivity>()
        }

//        binding.profileCart.setOnClickListener {
//            openActivity<CheckoutActivity>()
//        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                val user = session.getUser()
                                binding.user = user

                            }
                            else -> {

                            }
                        }
                    }
                }
            }
        }
    }

    private fun getUser() {
        viewModel.getProfile()
    }

    private fun getProduct() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.product.collect { product ->
                        adapter.submitList(product)
                    }
                }
            }
        }
    }


}