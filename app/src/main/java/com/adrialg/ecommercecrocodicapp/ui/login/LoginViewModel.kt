package com.adrialg.ecommercecrocodicapp.ui.login

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.toObject
import com.adrialg.ecommercecrocodicapp.api.ApiService
import com.adrialg.ecommercecrocodicapp.base.BaseViewModel
import com.adrialg.ecommercecrocodicapp.data.Const
import com.adrialg.ecommercecrocodicapp.data.Session
import com.adrialg.ecommercecrocodicapp.data.User
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val apiService: ApiService, private val gson: Gson, private val session: Session) : BaseViewModel() {

    fun login(phone: String, password: String ) = viewModelScope.launch {

        _apiResponse.send(ApiResponse(ApiStatus.LOADING))
        ApiObserver({ apiService.login(phone, password) },
            false, object : ApiObserver.ResponseListener {

                override suspend fun onSuccess(response: JSONObject) {
                    val token = response.getString("token")
                    val message = response.getString("info")
                    session.setValue(Const.TOKEN.API_TOKEN,token)
                    _apiResponse.send(ApiResponse(ApiStatus.SUCCESS, message = message))

                }

                //it works, lets go
                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = "Login Failed Error"))

                }
            })
    }

}