package com.bayupratama.spotgacor.ui.home.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.data.response.ApiResponseLogout
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import com.bayupratama.spotgacor.databinding.FragmentHomeBinding
import com.bayupratama.spotgacor.databinding.FragmentProfileBinding
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken
import com.bayupratama.spotgacor.ui.auth.login.LoginActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    private lateinit var sharedPreference: Sharedpreferencetoken

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = Sharedpreferencetoken(requireContext())
        binding.tvName.text = sharedPreference.getUserName()
        binding.tvEmail.text = sharedPreference.getUserEmail()
        val imagePhoto = sharedPreference.getProfileImageUrl()

        if (imagePhoto != null) {
            Glide.with(requireContext())
                .load(imagePhoto)
                .error(R.drawable.user_logo)
                .into(binding.ivPicture)

        // Gambar akan dimuat ke dalam ImageView
        } else {
            // Jika tidak ada URL gambar, bisa menampilkan gambar default atau kosong
            binding.ivPicture.setImageResource(R.drawable.user_logo)
        }

       binding.btnLogout.setOnClickListener {
            logoutUser()
        }

    }
    private fun logoutUser() {
        val apiService = ApiConfig.getApiService(requireContext())
        apiService.logout().enqueue(object : Callback<ApiResponseLogout> {
            override fun onResponse(
                call: Call<ApiResponseLogout>,
                response: Response<ApiResponseLogout>
            ) {
                if (response.isSuccessful) {
                    // Hapus token dan data pengguna dari SharedPreferences
                    val sharedPref = Sharedpreferencetoken(requireContext())
                    sharedPref.clearData()

                    // Arahkan pengguna kembali ke halaman login atau halaman utama
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish() // Menutup aktivitas saat ini
                } else {
                    // Menangani jika logout gagal
                    Toast.makeText(requireContext(), "Logout failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponseLogout>, t: Throwable) {
                // Menangani kegagalan koneksi API
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}