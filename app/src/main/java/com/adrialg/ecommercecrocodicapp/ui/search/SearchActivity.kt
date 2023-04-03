package com.adrialg.ecommercecrocodicapp.ui.search

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.adrialg.ecommercecrocodicapp.R
import com.adrialg.ecommercecrocodicapp.base.BaseActivity
import com.adrialg.ecommercecrocodicapp.data.Const
import com.adrialg.ecommercecrocodicapp.data.Product
import com.adrialg.ecommercecrocodicapp.data.Session
import com.adrialg.ecommercecrocodicapp.databinding.ActivitySearchBinding
import com.adrialg.ecommercecrocodicapp.databinding.HomeItemLayoutBinding
import com.adrialg.ecommercecrocodicapp.ui.detail.DetailActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>(R.layout.activity_search),
    SearchView.OnQueryTextListener{

    @Inject
    lateinit var session: Session

    private var product = ArrayList<Product?>()
    private var productAll = ArrayList<Product?>()

    private val adapter by lazy {
        ReactiveListAdapter<HomeItemLayoutBinding, Product>(R.layout.home_item_layout).initItem { position, data ->
            openActivity<DetailActivity> {
                putExtra(Const.PRODUCT.ID, data)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
        window.navigationBarColor = resources.getColor(R.color.light_grey)

        observe()
        getUser()
        getProduct()
        search()

        binding.recyclerSearch.adapter = adapter

        viewModel.productList()

        binding.searchBack.setOnClickListener {
            onBackPressed()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    getUser()
                    observe()
                }
            }
        }

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

                    viewModel.product.collect {
                        product.clear()
                        productAll.clear()

                        product.addAll(it)
                        productAll.addAll(it)
                        binding.recyclerSearch.adapter?.notifyDataSetChanged()
                    }

                }
            }
        }

    }

    private fun search(){
        binding.searchSearch.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                val filter = productAll.filter { it?.nameItem?.contains("$text", true) == true }
                Log.d("CekFilter", "Keyword $text Data : $filter")
                product.clear()
                filter.forEach {
                    product.add(it)
                }
                binding.recyclerSearch.adapter?.notifyDataSetChanged()
                binding.recyclerSearch.adapter?.notifyItemInserted(0)

            } else {
                product.clear()
                binding.recyclerSearch.adapter?.notifyDataSetChanged()
                product.addAll(productAll)
                Log.d("ceknoteall", "noteall : $productAll")
                binding.recyclerSearch.adapter?.notifyItemInserted(0)
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }


    override fun onQueryTextChange(newText: String?): Boolean {
        Timber.d(newText)
        return false
    }

}
