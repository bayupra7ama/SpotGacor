package com.bayupratama.spotgacor.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.data.response.DataItem
import com.bayupratama.spotgacor.databinding.ItemStoryBinding
import com.bayupratama.spotgacor.helper.formatDateString
import com.bumptech.glide.Glide

class StoryPagingAdapter :
    PagingDataAdapter<DataItem, StoryPagingAdapter.StoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: DataItem) {
            binding.tvUsername.text = story.user?.name
            binding.tvCaption.text = story.isiStory
            binding.tvCreatedAt.text = formatDateString(story.createdAt)
            // Load profile photo
            Glide.with(binding.root.context)
                .load(story.user?.profilePhotoUrl)
                .placeholder(R.drawable.user_logo) // Placeholder jika foto tidak tersedia
                .circleCrop()
                .into(binding.ivUserProvile)

            if (story.photoUrl != null){
                Glide.with(binding.root.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.loading3) // Placeholder jika foto tidak tersedia
                    .into(binding.ivPhoto)
            }else{
                binding.ivPhoto.visibility = View.GONE
            }
            // Load story image

        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
