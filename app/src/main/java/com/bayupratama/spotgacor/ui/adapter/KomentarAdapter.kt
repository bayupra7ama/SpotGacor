package com.bayupratama.spotgacor.ui.adapter




import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayupratama.spotgacor.R


import com.bayupratama.spotgacor.data.response.Ulasan
import com.bayupratama.spotgacor.databinding.UlasanItemBinding
import com.bumptech.glide.Glide

class KomentarAdapter(private val ulasanList: List<Ulasan>) :
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
}
