package com.bayupratama.spotgacor.ui.home.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Import untuk viewModels
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.data.response.ApiResponseLogout
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import com.bayupratama.spotgacor.databinding.FragmentProfileBinding
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken
import com.bayupratama.spotgacor.helper.getImageUri // Import fungsi getImageUri dari helper
import com.bayupratama.spotgacor.ui.auth.login.LoginActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreference: Sharedpreferencetoken
    private var tempImageUri: Uri? = null // URI sementara untuk gambar yang dipilih

    // Inisialisasi ProfileViewModel menggunakan delegated property viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

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


        binding.ivPicture.setOnClickListener {
            showImagePickerDialog() // Tampilkan dialog pilihan kamera/galeri
        }

        binding.btnChangePassword.setOnClickListener {
            showUpdatePasswordDialog()
        }


        // Set listener untuk tombol logout
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        // Observasi upload
        profileViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (binding.swipeRefreshLayout.isRefreshing.not()) {
                binding.progressBarImage.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        val userId = sharedPreference.getId() ?: 0
        profileViewModel.getUserById(userId)

        binding.swipeRefreshLayout.setOnRefreshListener {
            profileViewModel.getUserById(userId)
        }
        profileViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            Glide.with(requireContext())
                .load(user.profilePhotoUrl)
                .placeholder(R.drawable.loading2)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivPicture)

            binding.swipeRefreshLayout.isRefreshing = false // ⬅️ Tambahkan baris ini
        }

        profileViewModel.newProfilePhotoUrl.observe(viewLifecycleOwner) { url ->
            url?.let {
                Glide.with(requireContext())
                    .load(it)
                    .placeholder(R.drawable.loading2)
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Skip cache
                    .skipMemoryCache(true) // Juga skip cache memori
                    .into(binding.ivPicture)

            }
        }


    }


    /**
     * Menampilkan dialog pilihan sumber gambar (kamera atau galeri).
     */
    private fun showImagePickerDialog() {
        val options = arrayOf("Ambil dari Kamera", "Pilih dari Galeri")
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Ambil dari Kamera
                        tempImageUri =
                            getImageUri(requireContext()) // Menggunakan helper getImageUri
                        launcherCamera.launch(tempImageUri)
                    }

                    1 -> { // Pilih dari Galeri
                        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                }
            }
            .show()
    }

    /**
     * Activity Result Launcher untuk memilih gambar dari galeri.
     */
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            tempImageUri = uri // Simpan URI sementara
            showImageAndConfirmDialog() // Tampilkan pratinjau dan dialog konfirmasi
        } else {
            Toast.makeText(requireContext(), "Tidak ada gambar dipilih", Toast.LENGTH_SHORT).show()
            Log.d("ProfileFragment", "No media selected from gallery")
        }
    }

    /**
     * Activity Result Launcher untuk mengambil gambar dari kamera.
     */
    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            tempImageUri?.let { _ ->
                showImageAndConfirmDialog() // Tampilkan pratinjau dan dialog konfirmasi
            }
        } else {
            tempImageUri = null // Reset URI jika gagal
            Toast.makeText(requireContext(), "Gagal mengambil gambar", Toast.LENGTH_SHORT).show()
            Log.d("ProfileFragment", "Failed to take picture")
        }
    }

    private fun showImageAndConfirmDialog() {
        tempImageUri?.let { uri ->

            AlertDialog.Builder(requireContext())
                .setTitle("Simpan Foto Profil?")
                .setMessage("Apakah Anda ingin menyimpan gambar ini sebagai foto profil Anda?")
                .setPositiveButton("Simpan") { dialog, _ ->

                    profileViewModel.uploadProfilePhoto(uri) // panggil upload

                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    tempImageUri = null
                    dialog.dismiss()
                }
                .show()
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
                    sharedPreference.clearData()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Logout gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponseLogout>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    //    update password:
    private fun showUpdatePasswordDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_password, null)
        val oldPasswordEditText = dialogView.findViewById<EditText>(R.id.oldPasswordEditText)
        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.newPasswordEditText)
        val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.confirmPasswordEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Perbarui Kata Sandi")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val oldPassword = oldPasswordEditText.text.toString().trim()
                val newPassword = newPasswordEditText.text.toString().trim()
                val confirmPassword = confirmPasswordEditText.text.toString().trim()

                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(requireContext(), "Konfirmasi sandi tidak cocok", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                profileViewModel.updatePassword(oldPassword, newPassword, confirmPassword) { success, message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }

                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
