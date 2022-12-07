package com.example.storyappsubmission1.Data.Adapter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyappsubmission1.Activity.Main.DetailActivity
import com.example.storyappsubmission1.Data.Functon.withDateFormat
import com.example.storyappsubmission1.Data.Response.ListStoryR
import com.example.storyappsubmission1.Data.Response.StoryModel
import com.example.storyappsubmission1.R
import com.example.storyappsubmission1.databinding.ItemRowBinding
import java.util.TimeZone

class PagingAdapter : PagingDataAdapter<StoryModel, PagingAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class MyViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryModel) {
            binding.apply {
                tvItemName.text = data.name
                tvDate.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    data.createdAt.withDateFormat(TimeZone.getDefault().id)
                else
                    data.createdAt.take(10)

                Glide.with(itemView.context).load(data.photoUrl).into(tvItemPhoto)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.STORY_ID, data.id)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(tvItemPhoto, itemView.context.getString(R.string.image)),
                            Pair(tvItemName, itemView.context.getString(R.string.name)),
                            Pair(
                                tvDate,
                                itemView.context.getString(R.string.post)
                            )
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    interface OnItemClickCallback {
        fun onItemClicked(id: String?)
    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryModel>() {
            override fun areItemsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryModel,
                newItem: StoryModel
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}