package com.bayupratama.spotgacor.data.response

import com.google.gson.annotations.SerializedName

data class ApiAddStoryResponse(

	@field:SerializedName("data")
	val data: DataAddstory? = null,

	@field:SerializedName("meta")
	val meta: MetaAddStory? = null
)

data class MetaAddStory(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataAddstory(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("photo")
	val photo: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("isi_story")
	val isiStory: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
