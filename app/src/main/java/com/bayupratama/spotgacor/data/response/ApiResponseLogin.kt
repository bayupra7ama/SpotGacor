package com.bayupratama.spotgacor.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class ApiResponseLogin(

	@field:SerializedName("data")
	val data: DataLogin? = null,

	@field:SerializedName("meta")
	val meta: MetaLogin? = null
)


data class DataLogin(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("token_type")
	val tokenType: String? = null,

	@field:SerializedName("user")
	val user: UserLogin? = null
)


data class MetaLogin(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class UserLogin(

	@field:SerializedName("profile_photo_url")
	val profilePhotoUrl: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("roles")
	val roles: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Any? = null,

	@field:SerializedName("current_team_id")
	val currentTeamId: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("profile_photo_path")
	val profilePhotoPath: Any? = null,

	@field:SerializedName("two_factor_confirmed_at")
	val twoFactorConfirmedAt: Any? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("nomor_telpon")
	val nomorTelpon: String? = null
)
