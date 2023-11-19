package com.tegar.submissionaplikasistoryapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tegar.submissionaplikasistoryapp.R
import com.tegar.submissionaplikasistoryapp.data.remote.response.ListStoryItem
import com.tegar.submissionaplikasistoryapp.databinding.ItemStoriesBinding
import com.tegar.submissionaplikasistoryapp.util.DateUtils
import com.tegar.submissionaplikasistoryapp.util.formatFirstChar
import java.util.TimeZone

interface OnItemClickListener {
    fun onItemClick(item: ListStoryItem, bindingItem: ItemStoriesBinding)
}

class AdapterPagingListStories(private val listener: OnItemClickListener) :
    PagingDataAdapter<ListStoryItem, AdapterPagingListStories.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data, listener)
        }
    }

    class MyViewHolder(private val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem, listener: OnItemClickListener) {
            binding.itemName.text = data.name.formatFirstChar()
            binding.itemDesc.text = data.description.formatFirstChar()
            binding.itemCreated.text =
                DateUtils.formatDate(data.createdAt.toString(), TimeZone.getDefault().id)
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .placeholder(R.drawable.start)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.itemPhoto)

            itemView.setOnClickListener {
                listener.onItemClick(data, binding)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
