package com.bayupratama.spotgacor.data.response

import com.google.gson.annotations.SerializedName

data class ApiResponseLogout(

	@field:SerializedName("data")
	val data: Boolean? = null,

	@field:SerializedName("meta")
	val meta: MetaLogout? = null
)

data class MetaLogout(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
