package com.tegar.submissionaplikasistoryapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tegar.submissionaplikasistoryapp.R
import com.tegar.submissionaplikasistoryapp.data.local.preference.UserDataStore
import com.tegar.submissionaplikasistoryapp.data.remote.response.ListStoryItem
import com.tegar.submissionaplikasistoryapp.databinding.ActivityMainBinding
import com.tegar.submissionaplikasistoryapp.databinding.ItemStoriesBinding
import com.tegar.submissionaplikasistoryapp.ui.adapter.AdapterPagingListStories
import com.tegar.submissionaplikasistoryapp.ui.adapter.LoadingStateAdapter
import com.tegar.submissionaplikasistoryapp.ui.adapter.OnItemClickListener
import com.tegar.submissionaplikasistoryapp.ui.viewmodel.MainViewModel
import com.tegar.submissionaplikasistoryapp.ui.viewmodel.MainViewModelFactory
import com.tegar.submissionaplikasistoryapp.util.NetworkStatusChecker
import com.tegar.submissionaplikasistoryapp.util.ToastUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userDataStore: UserDataStore
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: AdapterPagingListStories

    private val addStoryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val storyAdded = data?.getBooleanExtra("story_added", false) ?: false
                if (storyAdded) {
                    adapter.refresh()
                    mainViewModel.isStoryAdded = true
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userDataStore = UserDataStore.getInstance(this)

        adapter = AdapterPagingListStories(object : OnItemClickListener {
            override fun onItemClick(item: ListStoryItem, bindingItem: ItemStoriesBinding) {
                val intent = Intent(this@MainActivity, DetailStory::class.java)
                intent.putExtra(DetailStory.EXTRA_DATA, item)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,
                        androidx.core.util.Pair.create(bindingItem.itemCardProfile, "profile"),
                        androidx.core.util.Pair.create(bindingItem.itemName, "nama"),
                        androidx.core.util.Pair.create(bindingItem.itemCreated, "created"),
                        androidx.core.util.Pair.create(bindingItem.itemDesc, "description"),
                        androidx.core.util.Pair.create(bindingItem.itemCardPhoto, "image"),
                    )
                startActivity(intent, optionsCompat.toBundle())
            }
        })
        binding.btnAdd.setOnClickListener(this)
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        getData()

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (positionStart == 0 && mainViewModel.isStoryAdded) {
                    binding.rvStories.scrollToPosition(0)
                }
            }
        })

        binding.refresh.setOnRefreshListener {
            getData()
            adapter.refresh()
            binding.refresh.isRefreshing = false
        }

    }

    @SuppressLint("DiscouragedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        supportActionBar?.title = getString(R.string.story_sync)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> openSetting()
            R.id.logout -> logout()
            R.id.maps -> openMaps()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add -> openAddStoryActivity()
        }
    }

    private fun getData() {
        if (NetworkStatusChecker.isConnectedToNetwork(this)) {
            mainViewModel.getStories().observe(this) {
                adapter.submitData(lifecycle, it)
            }
            ToastUtils.showToast(this, getString(R.string.data_has_been_updated))
        } else {
            mainViewModel.getStoriesOffline().observe(this) {
                adapter.submitData(lifecycle, it)
            }
            ToastUtils.showToast(this, getString(R.string.offline_mode))

        }
    }

    private fun openAddStoryActivity() {
        val intent = Intent(this, AddStoryActivity::class.java)
        addStoryResultLauncher.launch(intent)
    }

    private fun openSetting() {
        val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
        startActivity(intent)
    }

    private fun openMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(getString(R.string.confirm_logout))
        builder.setMessage(getString(R.string.are_you_sure_you_want_to_logout))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            lifecycleScope.launch {
                userDataStore.deleteSession()
                MainViewModelFactory.clearInstance()
                val intent = Intent(this@MainActivity, StartActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()

    }
}
