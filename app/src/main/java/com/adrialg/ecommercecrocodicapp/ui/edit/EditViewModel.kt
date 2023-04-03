package com.adrialg.ecommercecrocodicapp.ui.edit

import androidx.lifecycle.viewModelScope
import com.adrialg.ecommercecrocodicapp.api.ApiService
import com.adrialg.ecommercecrocodicapp.base.BaseViewModel
import com.adrialg.ecommercecrocodicapp.data.Session
import com.adrialg.ecommercecrocodicapp.data.User
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
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
class EditViewModel @Inject constructor(private val apiService: ApiService, private val gson: Gson, private val session: Session) : BaseViewModel() {

    //Update Profile W/O Image
    fun updateUser(name: String, phone: String) = viewModelScope.launch {
        ApiObserver({ apiService.updateProfile(name, phone) },
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

    //Update Profile W Image
    fun updateUserWPicture(name: String, phone: String, image: File) = viewModelScope.launch {
        val fileBody = image.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("photo", image.name, fileBody)
        ApiObserver({ apiService.updateProfilePicture(name, phone, filePart) },
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

}