package com.bayupratama.spotgacor.data.response

import com.google.gson.annotations.SerializedName

class ApiResponseListLokasi(

	@field:SerializedName("data")
	val responseData: LokasiData? = null,

	@field:SerializedName("meta")
	val responseMeta: LokasiMeta? = null
)

class LokasiMeta(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

class LokasiData(

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("data")
	val lokasiList: List<LokasiItem?>? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("next_page_url")
	val nextPageUrl: Any? = null,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: Any? = null,

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("last_page_url")
	val lastPageUrl: String? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("links")
	val links: List<LokasiLink?>? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
)

class LokasiLink(

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("url")
	val url: Any? = null
)

class LokasiItem(

	@field:SerializedName("rute")
	val rute: String? = null,

	@field:SerializedName("umpan")
	val umpan: String? = null,

	@field:SerializedName("perlengkapan")
	val perlengkapan: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("image_paths")
	val imagePaths: List<String>?,


	@field:SerializedName("ulasan_id")
	val ulasanId: Any? = null,

	@field:SerializedName("nama_tempat")
	val namaTempat: String? = null,

	@field:SerializedName("created_by")
	val createdBy: String? = null,

	@field:SerializedName("long")
	val jsonMemberLong: Any? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("medan")
	val medan: String? = null,

	@field:SerializedName("average_rating")
	val averageRating: String? = null,

	@field:SerializedName("jenis_ikan")
	val jenisIkan: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("lat")
	val lat: Any? = null
)
