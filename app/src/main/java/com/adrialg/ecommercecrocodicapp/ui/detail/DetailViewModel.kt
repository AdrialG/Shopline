package com.adrialg.ecommercecrocodicapp.ui.detail

import androidx.lifecycle.viewModelScope
import com.adrialg.ecommercecrocodicapp.api.ApiService
import com.adrialg.ecommercecrocodicapp.base.BaseViewModel
import com.adrialg.ecommercecrocodicapp.data.ImageSlide
import com.adrialg.ecommercecrocodicapp.data.Product
import com.adrialg.ecommercecrocodicapp.data.Session
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val apiService: ApiService, private val gson: Gson, private val session: Session): BaseViewModel() {


    private val _product = MutableSharedFlow<Product>()
    val product = _product.asSharedFlow()
    val imageSlider = MutableSharedFlow<List<ImageSlide>>()

    fun getProduct(id: Int) = viewModelScope.launch {
        ApiObserver({apiService.getProductById(id)},
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<Product>(gson)
                    _product.emit(data)
                    val image = response.getJSONArray("image_sliders").toList<ImageSlide>(gson)
                    imageSlider.emit(image)
                    _apiResponse.send(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())

                }
            })
    }

}