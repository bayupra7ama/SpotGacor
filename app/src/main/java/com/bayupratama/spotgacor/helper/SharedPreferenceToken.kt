package com.bayupratama.spotgacor.helper

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject


class Sharedpreferencetoken (context: Context) {
    private val sharedpreference : SharedPreferences = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE)
    private val editor : SharedPreferences.Editor = sharedpreference.edit()

    fun saveToken(token : String){
        editor.putString("token", token)
        editor.apply()
    }

    fun getToken() : String?{
        return sharedpreference.getString("token", null)
    }

    fun getUsername() : String?{
        return sharedpreference.getString("name", null)
    }
    fun saveUserData(name: String?, email: String?, url: String?, id: String?) {
        // Simpan data pengguna
        editor.putString("name", name)
        editor.putString("email", email)
        editor.putString("id",id)
        // Simpan URL gambar profil
        url?.let {
            editor.putString("profileImageUrl", url)
        }

        editor.apply()
    }

    // Mengambil data pengguna
    fun getUserName(): String? {
        return sharedpreference.getString("name", null)
    }

    fun getId(): Int {
        val idStr = sharedpreference.getString("id", "0")
        return try {
            idStr?.toInt() ?: 0
        } catch (e: Exception) {
            Log.e("SharedPref", "Gagal convert id ke Int: ${e.message}")
            0
        }
    }

    fun saveProfileImageUrl(url: String) {
        val editor = sharedpreference.edit()
        editor.putString("profileImageUrl", url)
        editor.apply()
    }

    fun getUserId(): Int {
        val userJson = sharedpreference.getString("user", null)
        return if (userJson != null) {
            val userObj = JSONObject(userJson)
            userObj.getInt("id")
        } else 0
    }




    fun getUserEmail(): String? {
        return sharedpreference.getString("email", null)
    }



    // Mengambil URL gambar profil
    fun getProfileImageUrl(): String? {
        val url = sharedpreference.getString("profileImageUrl", null)
        Log.d("SharedPref", "Mengambil URL foto profil: $url")
        return url
    }

    fun clearData(){
        editor.remove("token")
        editor.remove("name")
        editor.remove("email")
        editor.remove("nomorHp")
        editor.remove("profileImageUrl")
        editor.apply()
    }
}