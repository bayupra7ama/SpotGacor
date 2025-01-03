package com.bayupratama.spotgacor.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken
import com.bayupratama.spotgacor.ui.auth.login.LoginActivity
import com.bayupratama.spotgacor.ui.home.MainActivity


class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sharedpreferencetoken: Sharedpreferencetoken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi SharedPreferences
        sharedpreferencetoken = Sharedpreferencetoken(this)

        Handler(Looper.getMainLooper()).postDelayed({
            checkToken()
        }, 3000)
    }

    private fun checkToken() {
        val token = sharedpreferencetoken.getToken()
        if (token.isNullOrEmpty()) {
            // Jika token kosong, arahkan ke RegisterActivity atau LoginActivity
            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
        } else {
            // Jika token tersedia, arahkan ke MainActivity
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        }
        finishAffinity() // Tutup SplashScreenActivity
    }
}
