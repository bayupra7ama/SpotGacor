package com.bayupratama.spotgacor.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bayupratama.spotgacor.data.response.LokasiItem
import com.bayupratama.spotgacor.databinding.ItemLokasiBinding
import com.bumptech.glide.Glide

class LokasiPagingAdapter(
    private val onItemClick: (LokasiItem) -> Unit
) : PagingDataAdapter<LokasiItem, LokasiPagingAdapter.LokasiViewHolder>(
    LokasiDiffCallback()
) {

    override fun onBindViewHolder(holder: LokasiViewHolder, position: Int) {
        val lokasi = getItem(position)
        if (lokasi != null) {
            holder.bind(lokasi, onItemClick)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LokasiViewHolder {
        val binding = ItemLokasiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LokasiViewHolder(binding)
    }

    class LokasiViewHolder(private val binding: ItemLokasiBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lokasi: LokasiItem, onItemClick: (LokasiItem) -> Unit) {
            // Mengatur nama lokasi
            binding.nameTextView.text = lokasi.namaTempat

            // Mengatur deskripsi lokasi
            binding.descriptionTextView.text = lokasi.jenisIkan

            // Mengatur rating lokasi
            binding.ratingBar.rating = lokasi.averageRating?.toFloat() ?: 0f

            // Mengatur gambar lokasi
            val imageUrl = lokasi.imagePaths?.firstOrNull()?.let { path ->
                cleanUrl("https://brief-sawfly-square.ngrok-free.app$path")
            }

            Glide.with(binding.root.context)
                .load(imageUrl)
                .fitCenter()
                .into(binding.avatarImageView)

            // Klik listener untuk item
            binding.root.setOnClickListener {
                onItemClick(lokasi)
            }
        }

        private fun cleanUrl(url: String): String {
            return url.replace("\\", "") // Hapus semua backslash
        }
    }

    class LokasiDiffCallback : DiffUtil.ItemCallback<LokasiItem>() {
        override fun areItemsTheSame(oldItem: LokasiItem, newItem: LokasiItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LokasiItem, newItem: LokasiItem): Boolean {
            return oldItem == newItem
        }
    }
}
