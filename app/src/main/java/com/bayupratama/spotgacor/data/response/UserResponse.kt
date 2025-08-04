package com.bayupratama.spotgacor.data.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val meta: MetaUser,
    val data: UserData
)

data class MetaUser(
    val code: Int,
    val status: String,
    val message: String
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("profile_photo_url")
    val profilePhotoUrl: String
)
