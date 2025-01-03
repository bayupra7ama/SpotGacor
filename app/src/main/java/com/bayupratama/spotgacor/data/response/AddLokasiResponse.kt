package com.bayupratama.spotgacor.data.response

data class AddLokasiResponse(
    val message: String,
    val data: AddLokasiData // Ganti LokasiData menjadi AddLokasiData
)

data class AddLokasiData(
    val user_id: Int,
    val nama_tempat: String,
    val alamat: String,
    val lat: String,
    val long: String,
    val jenis_ikan: String?,
    val perlengkapan: String,
    val rute: String,
    val umpan: String,
    val image_paths: List<String>
)
