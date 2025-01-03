package com.bayupratama.spotgacor.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bayupratama.spotgacor.data.response.ApiResponseLogin
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import com.bayupratama.spotgacor.databinding.ActivityLoginBinding
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken
import com.bayupratama.spotgacor.ui.auth.register.RegisterActivity
import com.bayupratama.spotgacor.ui.home.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedpreferencetoken: Sharedpreferencetoken

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedpreferencetoken = Sharedpreferencetoken(this)

        binding.btnLogin.setOnClickListener {
            val email = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()

            if ( email.isNotEmpty() && password.isNotEmpty()) {
                login(email , password)
            } else {
                Toast.makeText(this@LoginActivity, "Lengkapi semuanya", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvDaftar.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login( email: String, password: String) {
        showLoading(true)
        val client = ApiConfig.getApiService(this).postLogin(email,password)
        client.enqueue(object :Callback<ApiResponseLogin>{
            override fun onResponse(
                call: Call<ApiResponseLogin>,
                response: Response<ApiResponseLogin>
            ) {
                showLoading(false)
                if (response.isSuccessful && response.body() != null){
                    showLoading(false)
                    val token = response.body()?.data?.accessToken
                    val response = response.body()?.data?.user

                    if (!token.isNullOrEmpty()) {
                        // Simpan token ke SharedPreferences
                        sharedpreferencetoken.saveToken(token)
                        val username = response?.name
                        val emai = response?.email
                        val photoProfile = response?.profilePhotoUrl

                        sharedpreferencetoken.saveUserData(username,emai,photoProfile)
                    }

                    Toast.makeText(
                        this@LoginActivity,
                        "Registrasi berhasil!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Arahkan ke MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                    else {
                    Log.e("Gagal", "onFailure: ${response.message().toString()}")
                    Toast.makeText(
                        this@LoginActivity,
                        "Gagal masuk: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }

            override fun onFailure(call: Call<ApiResponseLogin>, t: Throwable) {
                Log.e("TAG", "onFailure: ${t.message}")
                showLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    "Terjadi kesalahan: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }


        })

    }

    private fun showLoading(isLoading:Boolean){
        if (isLoading){
            binding.progresbar.visibility = View.VISIBLE
        }else{
            binding.progresbar.visibility = View.GONE
        }
    }
}