package com.bayupratama.spotgacor.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class ApiResponseLokasiDetail(
    val meta: MetaDetail,
    val data: LokasiDetail
)


data class MetaDetail(
    val code: Int,
    val status: String,
    val message: String
)


data class LokasiDetail(
    val lokasi: Lokasi,
    val average_rating: Double,
    val ulasan: List<Ulasan>
)


data class Lokasi(
    val id: Int,
    val nama_tempat: String,
    val alamat: String,
    val created_by: String,
    val lat: Double,
    val long: Double,
    val photo: Any, // Assuming photo is a list of strings (URLs)
    val rute: String,
    val perlengkapan: String,
    val umpan: String,
    val jenis_ikan: String,
    val medan:String
)


data class Ulasan(
    val ulsan_id: Int,
    val user_id: Int, // ✅ tambahkan ini

    val user: String,
    val photo_profile: String?,
    val rating: Int,
    val komentar: String,
    val photo: String
)
