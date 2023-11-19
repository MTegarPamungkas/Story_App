package com.tegar.submissionaplikasistoryapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.core.app.ActivityOptionsCompat
import com.tegar.submissionaplikasistoryapp.data.local.preference.UserDataStore
import com.tegar.submissionaplikasistoryapp.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var userDataStore: UserDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        userDataStore = UserDataStore.getInstance(this)

        binding.SplashScreenImage.alpha = 0f
        binding.SplashScreenImage.animate().setDuration(ANIMATION_DURATION).alpha(1f).withEndAction {
            lifecycleScope.launch {
                var nextIntent: Intent
                userDataStore.getUserData().collect { data ->
                    nextIntent = if (data.userId.isNotEmpty() && data.name.isNotEmpty() && data.token.isNotEmpty()) {
                        Intent(this@SplashScreen, MainActivity::class.java)
                    } else {
                        Intent(this@SplashScreen, StartActivity::class.java)
                    }
                    val options = ActivityOptionsCompat.makeCustomAnimation(this@SplashScreen, android.R.anim.fade_in, android.R.anim.fade_out)
                    startActivity(nextIntent, options.toBundle())
                    finish()
                }
            }
        }
    }

    companion object {
        const val ANIMATION_DURATION = 1500L
    }
}
