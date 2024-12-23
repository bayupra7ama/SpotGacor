package com.bayupratama.spotgacor.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayupratama.spotgacor.databinding.ItemSliderBinding
import com.bumptech.glide.Glide

class ImageSliderAdapter(private val images: List<String>) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = "https://brief-sawfly-square.ngrok-free.app" + images[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.binding.imageView)
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(val binding: ItemSliderBinding) : RecyclerView.ViewHolder(binding.root)
}
