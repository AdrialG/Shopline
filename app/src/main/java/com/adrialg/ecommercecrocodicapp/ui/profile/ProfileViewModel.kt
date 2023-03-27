package com.adrialg.ecommercecrocodicapp.ui.profile

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.adrialg.ecommercecrocodicapp.api.ApiService
import com.adrialg.ecommercecrocodicapp.base.BaseViewModel
import com.adrialg.ecommercecrocodicapp.data.Session
import com.adrialg.ecommercecrocodicapp.data.User
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val apiService: ApiService, private val gson: Gson, private val session: Session) : BaseViewModel() {



    // get Profile
    fun getProfile(
    ) = viewModelScope.launch {
        ApiObserver({ apiService.getProfile() },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)

                    //Use When Channel
                    _apiResponse.send(ApiResponse().responseSuccess())
                    session.saveUser(data)


                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                    //Use When Channel
                    _apiResponse.send(ApiResponse().responseError())

                }
            })
    }

    fun updateUser(name: String, phone_number: String) = viewModelScope.launch {
        ApiObserver({ apiService.updateProfile(name, phone_number) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    _apiResponse.send(ApiResponse().responseSuccess("Profile Updated"))
                    session.saveUser(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())
                }

            })
    }

    fun updateUserWithPhoto(name: String, phone_number: String, photo: File) =
        viewModelScope.launch {
            val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("image", photo.name, fileBody)
            ApiObserver({ apiService.updateProfilePicture(name, phone_number, filePart) },
                false, object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                        _apiResponse.send(ApiResponse().responseSuccess("Profile Updated"))
                        session.saveUser(data)
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.send(ApiResponse().responseError())
                    }

                })
        }

    fun logout() = viewModelScope.launch {
        ApiObserver({ apiService.logout() },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    session.clearAll()
                }
            })
    }

}