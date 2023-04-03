package com.adrialg.ecommercecrocodicapp.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.adrialg.ecommercecrocodicapp.R
import com.adrialg.ecommercecrocodicapp.base.BaseActivity
import com.adrialg.ecommercecrocodicapp.data.Const
import com.adrialg.ecommercecrocodicapp.data.ImageSlide
import com.adrialg.ecommercecrocodicapp.data.Product
import com.adrialg.ecommercecrocodicapp.databinding.ActivityDetailBinding
import com.adrialg.ecommercecrocodicapp.databinding.DetailColourItemLayoutBinding
import com.adrialg.ecommercecrocodicapp.databinding.DetailSizeItemLayoutBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.snacked
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding, DetailViewModel>(R.layout.activity_detail) {

    var product: Product? = null
    private var listColor = ArrayList<Product.Variant?>()
    private var selectColor: Product.Variant? = null
    private var listSize = ArrayList<Product.Sizes?>()
    private var selectSize: Product.Sizes? = null

    private val adapterVariant by lazy {
        object : ReactiveListAdapter<DetailColourItemLayoutBinding, Product.Variant>(R.layout.detail_colour_item_layout) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<DetailColourItemLayoutBinding, Product.Variant>,
                position: Int
            ) {
                //Variant
                listColor[position]?.let { data ->
                    holder.binding.data = data
                    holder.binding.variantCard.setBackgroundColor(
                        if (data.selected) applicationContext.getColor(R.color.main_orange)
                        else applicationContext.getColor(R.color.white)
                    )

                    holder.itemView.setOnClickListener {
                        listColor.forEachIndexed { index, variant ->
                            variant?.selected = index == position
                        }

                        notifyDataSetChanged()
                        selectColor = data
                        condititonForColor(data.id)
                        Timber.d("CekListColors: $listColor")
                        println("CekListColors: $listColor")
                    }
                }
            }
        }.initItem()
    }

    private val adapterSize by lazy {
        object : ReactiveListAdapter<DetailSizeItemLayoutBinding, Product.Sizes>(R.layout.detail_size_item_layout) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<DetailSizeItemLayoutBinding, Product.Sizes>,
                position: Int
            ) {
                //Size
                listSize[position]?.let { data ->
                    holder.binding.data = data
                    holder.binding.sizeCard.setBackgroundColor(
                        if (data.selected) applicationContext.getColor(R.color.main_orange)
                        else applicationContext.getColor(R.color.white)
                    )

                    holder.itemView.setOnClickListener {
                        listSize.forEachIndexed { index, Size ->
                            Size?.selected = index == position
                        }

                        notifyDataSetChanged()
                        selectSize = data
                        Timber.d("CekListColors: $listSize")
                        println("CekListColors: $listSize")
                    }
                }
            }

        }.initItem()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageList = ArrayList<SlideModel>()
        window.statusBarColor = resources.getColor(R.color.white)

        productData()
        observe()

        binding.detailBack.setOnClickListener {
            onBackPressed()
        }

        binding.detailVariantRecycler.setOnClickListener {
            binding.detailSizeRecycler.isVisible = true
        }

        val imageSlider = binding.detailSlider
        imageSlider.setImageList(imageList)
        binding.detailVariantRecycler.adapter = adapterVariant
        binding.detailSizeRecycler.adapter = adapterSize

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
//                                tos("Product Added to Cart")
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Added To Card")
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> {
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                        }

                    }
                }

                launch {
                    viewModel.imageSlider.collect {
                        initSlider(it)
                    }
                }
                launch {
                    viewModel.product.collect { product ->
                        binding.data = product
                        adapterVariant.submitList(product.variants)
                        listColor.clear()
                        product.variants?.let { list ->
                            listColor.addAll(list)
                        }

                        adapterSize.submitList(product.sizes)
                        listSize.clear()
                        product.sizes?.let { list ->
                            listSize.addAll(list)
                        }

                        product.imageSliders?.let { initSlider(it) }
                        println("ListVariant: ${product.variants}")
                        println("ListSizes: ${product.sizes}")
                    }
                }


            }
        }
    }

    private fun productData() {
        //Receiving TourData
        val data = intent.getParcelableExtra<Product>(Const.PRODUCT.ID)
        data?.id?.let { viewModel.getProduct(it) }

    }

    private fun condititonForColor(idVarian: Int?) {
        if (selectColor == null) {
            binding.detailSizeRecycler.visibility = View.INVISIBLE
        } else {

            binding.detailSizeRecycler.visibility = View.VISIBLE
//            adapterColor.submitList(listColor)
            val filterSize = listSize.filter {
                it?.variantId == idVarian
            }
//            filterListSize.clear()
//            filterListSize.addAll(filterSize)
            adapterSize.submitList(filterSize)
        }
    }

    private fun initSlider(data: List<ImageSlide>) {
        val imageList = ArrayList<SlideModel>()
        data.forEach {
            imageList.add(SlideModel(it.imageS))
        }
        binding.detailSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
    }

}