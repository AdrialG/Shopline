package com.adrialg.ecommercecrocodicapp.ui.home

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.adrialg.ecommercecrocodicapp.api.ApiService
import com.adrialg.ecommercecrocodicapp.base.BaseViewModel
import com.adrialg.ecommercecrocodicapp.data.Product
import com.adrialg.ecommercecrocodicapp.data.Session
import com.adrialg.ecommercecrocodicapp.data.User
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val apiService: ApiService, private val gson: Gson, private val session: Session) : BaseViewModel() {

    private val _product = MutableSharedFlow<List<Product>>()
    val product = _product.asSharedFlow()

    //Pull Profile
    fun getProfile(
    ) = viewModelScope.launch {
        ApiObserver({ apiService.getProfile() },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    _apiResponse.send(ApiResponse().responseSuccess())
                    session.saveUser(data)

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())

                }
            })
    }

    //Product List
    fun productList(
    ) = viewModelScope.launch {
        ApiObserver({ apiService.getProduct() },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Product>(gson)
                    _apiResponse.send(ApiResponse().responseSuccess())
                    _product.emit(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())

                }
            })
    }
}