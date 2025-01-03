package com.bayupratama.spotgacor.data.response

import com.google.gson.annotations.SerializedName

data class ApiAddUlsanResponse(

	@field:SerializedName("data")
	val data: DataAddUlasan? = null,

	@field:SerializedName("meta")
	val meta: MetaAddUlasan? = null
)

data class MetaAddUlasan(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataAddUlasan(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("komentar")
	val komentar: String? = null,

	@field:SerializedName("rating")
	val rating: String? = null,

	@field:SerializedName("photo")
	val photo: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("lokasi_id")
	val lokasiId: Int? = null
)
