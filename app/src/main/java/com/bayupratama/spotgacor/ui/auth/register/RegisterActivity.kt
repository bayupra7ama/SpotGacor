package com.bayupratama.spotgacor.ui.auth.register
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.data.response.ApiResponseRegister
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import com.bayupratama.spotgacor.databinding.ActivityRegisterBinding
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken
import com.bayupratama.spotgacor.ui.auth.login.LoginActivity
import com.bayupratama.spotgacor.ui.home.MainActivity

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var sharedpreferencetoken: Sharedpreferencetoken

    private lateinit var binding : ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_actifity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedpreferencetoken = Sharedpreferencetoken(this)
        binding.btnLogin.setOnClickListener {
            val name = binding.edtNama.text.toString()
            val email = binding.edtEmail.text.toString()
            val nomorHp = binding.edtNomorH.text.toString()
            val password = binding.edtPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && nomorHp.isNotEmpty() && password.isNotEmpty()) {
                register(name, email, nomorHp, password)
            } else {
                Toast.makeText(this@RegisterActivity, "Lengkapi semuanya", Toast.LENGTH_SHORT).show()
            }



        }

        binding.tvLogin.setOnClickListener{
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading(isLoading:Boolean){
        if (isLoading){
            binding.progresbar.visibility = View.VISIBLE
        }else{
            binding.progresbar.visibility = View.GONE
        }
    }

    private fun register(name: String, email: String, nomorHp: String, password: String) {
        showLoading(true)
        val client = ApiConfig.getApiService(this).postRegister(name, email, nomorHp, password, password)
        client.enqueue(object : Callback<ApiResponseRegister> {
            override fun onResponse(
                call: Call<ApiResponseRegister>,
                response: Response<ApiResponseRegister>
            ) {
                showLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val token = responseBody.data?.accessToken // Pastikan respons memiliki field `token`
                    val response = responseBody.data?.user
                    if (!token.isNullOrEmpty()) {
                        // Simpan token ke SharedPreferences
                        sharedpreferencetoken.saveToken(token)
                        val username = response?.name
                        val emai = response?.email
                        val photoProfile = response?.profilePhotoUrl
                        val id = response?.id.toString()

                        sharedpreferencetoken.saveUserData(username,emai,photoProfile,id)
                    }

                    Toast.makeText(
                        this@RegisterActivity,
                        "Registrasi berhasil!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Arahkan ke MainActivity
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup aktivitas registrasi setelah sukses
                } else {
                    Log.e("Gagal", "onFailure: ${response.message()}")
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registrasi gagal: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponseRegister>, t: Throwable) {
                Log.e("TAG", "onFailure: ${t.message}")
                showLoading(false)
                Toast.makeText(
                    this@RegisterActivity,
                    "Terjadi kesalahan: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
};