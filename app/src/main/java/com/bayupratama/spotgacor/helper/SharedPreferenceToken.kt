package com.bayupratama.spotgacor.helper

import android.content.Context
import android.content.SharedPreferences


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
    fun saveUserData(name: String?, email: String?, profileImageUrl: String?) {
        // Simpan data pengguna
        editor.putString("name", name)
        editor.putString("email", email)

        // Simpan URL gambar profil
        profileImageUrl?.let {
            editor.putString("profileImageUrl", it)
        }

        editor.apply()
    }

    // Mengambil data pengguna
    fun getUserName(): String? {
        return sharedpreference.getString("name", null)
    }

    fun getUserEmail(): String? {
        return sharedpreference.getString("email", null)
    }



    // Mengambil URL gambar profil
    fun getProfileImageUrl(): String? {
        return sharedpreference.getString("profileImageUrl", null)
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