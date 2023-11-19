package com.tegar.submissionaplikasistoryapp.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tegar.submissionaplikasistoryapp.R
import com.tegar.submissionaplikasistoryapp.data.remote.response.ListStoryItem
import com.tegar.submissionaplikasistoryapp.databinding.ActivityDetailStoryBinding
import com.tegar.submissionaplikasistoryapp.util.DateUtils
import com.tegar.submissionaplikasistoryapp.util.formatFirstChar
import java.util.TimeZone

class DetailStory : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_DATA, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_DATA)
        }

        data?.let {
            Glide.with(this)
                .load(it.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.start)
                .into(binding.detailPhoto)
            binding.detailName.text = it.name.formatFirstChar()
            binding.detailDesc.text = it.description.formatFirstChar()
            binding.detailCreated.text =
                DateUtils.formatDate(it.createdAt.toString(), TimeZone.getDefault().id)
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}
