import com.bumptech.glide.load.engine.DiskCacheStrategy

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
import com.squareup.picasso.Picasso

class StoryPagingAdapter :
    PagingDataAdapter<DataItem, StoryPagingAdapter.StoryViewHolder>(DiffCallback) {

    // Membuat ViewHolder untuk item cerita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    // Mengikat data cerita ke ViewHolder
    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position) // Mendapatkan cerita di posisi tertentu
        story?.let { holder.bind(it) } // Mengikat data ke ViewHolder
    }

    // ViewHolder untuk item cerita
    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {

        private val retryDelayMillis = 3000L // Retry delay after failure (3 detik)

        fun bind(story: DataItem) {
            binding.tvUsername.text = story.user?.name
            binding.tvCaption.text = story.isiStory
            binding.tvCreatedAt.text = formatDateString(story.createdAt)

            // Memuat foto profil dengan retry dan cache
            if (story.user?.profilePhotoUrl != null) {
                Glide.with(binding.root.context)
                    .load(story.user.profilePhotoUrl) // Menggunakan URL gambar profil
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Menggunakan cache untuk menghindari pemuatan ulang gambar
                    .placeholder(R.drawable.user_logo) // Menampilkan placeholder sementara
                    .circleCrop() // Memotong gambar menjadi bulat
                    .error(R.drawable.user_logo) // Menampilkan gambar error jika gagal memuat
                    .into(binding.ivUserProvile) // Memasukkan gambar ke dalam ImageView
            }

            val url = "https://brief-sawfly-square.ngrok-free.app/"
            val photoUrl = url + story.photoUrl

            // Memuat foto cerita dengan retry dan cache
            if (story.photoUrl != null) {
                binding.ivPhoto.visibility = View.VISIBLE
                Glide.with(binding.root.context)
                    .load(photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache untuk menyimpan gambar secara lokal
                    .into(binding.ivPhoto)
            }else{
                binding.ivPhoto.visibility = View.GONE

            }
        }
    }

    // Membandingkan item untuk mendeteksi perubahan dan menentukan apakah item sama
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem.id == newItem.id // Memeriksa apakah item yang lama dan baru sama
            }

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem // Memeriksa apakah konten item yang lama dan baru sama
            }
        }
    }
}



