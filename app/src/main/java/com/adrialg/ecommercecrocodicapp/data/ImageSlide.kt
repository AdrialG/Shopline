package com.adrialg.ecommercecrocodicapp.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageSlide(
    @Expose
    @SerializedName("id")
    val id: Int?,
    @Expose
    @SerializedName("product_id")
    val productId: Int?,
    @Expose
    @SerializedName("image")
    val imageS: String?,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?,
) : Parcelable