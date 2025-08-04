package com.bayupratama.spotgacor.data.response

data class EditUlasanResponse(
    val meta: Meta,
    val data: UlasanData
)



data class UlasanData(
    val id: Int,
    val user_id: Int,
    val lokasi_id: Int,
    val rating: Int,
    val komentar: String,
    val photo: String?,
    val created_at: String,
    val updated_at: String
)
