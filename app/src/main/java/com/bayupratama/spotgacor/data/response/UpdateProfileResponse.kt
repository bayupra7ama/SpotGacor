package com.bayupratama.spotgacor.data.response



import com.google.gson.annotations.SerializedName

data class PhotoResponse(
    @field:SerializedName("meta") // Perhatikan bahwa respons backend Anda memiliki 'meta' di level root
    val meta: MetaUpdatePhoto? = null, // Tambahkan data class Meta jika belum ada

    @field:SerializedName("data")
    val data: PhotoData? = null
)

data class MetaUpdatePhoto( // Tambahkan data class ini jika belum ada
    @field:SerializedName("code")
    val code: Int,
    @field:SerializedName("status")
    val status: String,
    @field:SerializedName("message")
    val message: String
)

data class PhotoData(
    @field:SerializedName("profile_photo_url")
    val profilePhotoUrl: String
)

