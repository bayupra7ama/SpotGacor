package com.bayupratama.spotgacor.helper

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

private const val MAXIMAL_SIZE = 1000000 //1 MB
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
fun uriToFile(uri: Uri, context: Application): File? {
    val contentResolver = context.contentResolver
    val tempFile = File.createTempFile("temp_image", null, context.cacheDir)
    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return tempFile
}
fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        // content://media/external/images/media/1000000062
        // storage/emulated/0/Pictures/MyCamera/20230825_155303.jpg
    }
    return uri ?: getImageUriForPreQ(context)


}
private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "com.bayupratama.spotgacor.fileprovider",
        imageFile
    )

}


fun formatDateString(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "Tanggal tidak tersedia"

    return try {
        // Format input sesuai dengan respons API
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC") // Sesuaikan zona waktu

        // Parse string ke objek Date
        val date: Date = inputFormat.parse(dateString) ?: return "Format tanggal salah"

        // Format output yang ramah pengguna
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        "Tanggal tidak valid"
    }
}


