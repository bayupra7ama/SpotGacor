import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.data.response.DataItem
import com.bayupratama.spotgacor.databinding.ItemStoryBinding
import com.bayupratama.spotgacor.helper.formatDateString
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException

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

        private val retryDelayMillis = 3000L // Retry delay after failure (3 seconds)

        fun bind(story: DataItem) {
            binding.tvUsername.text = story.user?.name
            binding.tvCaption.text = story.isiStory
            binding.tvCreatedAt.text = formatDateString(story.createdAt)

            // Memuat foto profil dengan retry

            if (story.user?.profilePhotoUrl != null){
                Glide.with(binding.root.context)
                    .load(binding.ivUserProvile)
                    .error(R.drawable.user_logo)
                    .into(binding.ivUserProvile)

                }


            val url = "https://brief-sawfly-square.ngrok-free.app/"
            val photoUrl = url + story.photoUrl
            // Memuat foto cerita dengan retry
            if (story.photoUrl != null) {
                Glide.with(binding.root.context)
                    .load(photoUrl)
                    .error(R.drawable.ic_placeholder)
                    .into(binding.ivPhoto)
            }else{

                binding.ivPhoto.visibility = View.GONE
            }

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
