package com.bayupratama.spotgacor.ui.adapter




import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayupratama.spotgacor.R


import com.bayupratama.spotgacor.data.response.Ulasan
import com.bayupratama.spotgacor.databinding.UlasanItemBinding
import com.bayupratama.spotgacor.ui.home.ui.lokasi.AddUlasanActivity
import com.bumptech.glide.Glide

class KomentarAdapter(private var ulasanList: List<Ulasan>,
                      private val currentUserId: Int
) :
    RecyclerView.Adapter<KomentarAdapter.KomentarViewHolder>() {

    // ViewHolder untuk item komentar
    inner class KomentarViewHolder(private val binding: UlasanItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ulasan: Ulasan) {
            // Mengatur data komentar ke view
            binding.tvUserName.text = ulasan.user
            binding.tvComment.text = ulasan.komentar
            binding.ratingBar.rating = ulasan.rating.toFloat()

            // Jika ada foto profil pengguna, tampilkan gambar (jika tidak null)
            if (ulasan.photo_profile != null) {
                val photo_url = "https://brief-sawfly-square.ngrok-free.app/storage/"+ ulasan.photo_profile
                Glide.with(binding.root.context)
                    .load(photo_url)
                    .into(binding.ivProfilePicture)
            }

            if (ulasan.photo != null) {
                val photo_url = "https://brief-sawfly-square.ngrok-free.app/storage/"+ ulasan.photo
                Glide.with(binding.root.context)

                    .load(photo_url)
                    .into(binding.ivReviewImage)
                binding.ivReviewImage.visibility = View.VISIBLE
            }

            Log.d("UlasanAdapter", "ulasan.user_id = ${ulasan.user_id}, currentUserId = $currentUserId")

            if (ulasan.user_id == currentUserId) {
                binding.btnEditKomentar.visibility = View.VISIBLE
                Log.d("UlasanAdapter", "✅ Menampilkan tombol edit untuk user ini")
            } else {
                binding.btnEditKomentar.visibility = View.GONE
                Log.d("UlasanAdapter", "❌ Tidak menampilkan tombol edit untuk user ini")
            }

            binding.btnEditKomentar.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, AddUlasanActivity::class.java)
                intent.putExtra("isEdit", true)
                intent.putExtra("ulasanId", ulasan.ulsan_id) // ⬅️ pastikan kamu sudah mengirim ID ulasan dari backend
                intent.putExtra("rating", ulasan.rating)
                intent.putExtra("komentar", ulasan.komentar)
                context.startActivity(intent)
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KomentarViewHolder {
        // Menyusun item komentar menggunakan binding
        val binding = UlasanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KomentarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KomentarViewHolder, position: Int) {
        // Mengikat data komentar dengan ViewHolder
        holder.bind(ulasanList[position])
    }

    override fun getItemCount(): Int {
        return ulasanList.size
    }
    fun updateData(newData: List<Ulasan>) {
        ulasanList = newData
        notifyDataSetChanged()
    }
}
