package com.tegar.submissionaplikasistoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tegar.submissionaplikasistoryapp.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var activityStartBinding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityStartBinding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(activityStartBinding.root)

        supportActionBar?.hide()

        activityStartBinding.btnRegisterStart.setOnClickListener {
            val bottomRegisterFragment = RegisterDialogFragment()
            bottomRegisterFragment.isCancelable = false
            bottomRegisterFragment.show(supportFragmentManager, bottomRegisterFragment.tag)
        }
        activityStartBinding.btnLoginStart.setOnClickListener {
            val bottomLoginFragment = LoginDialogFragment()
            bottomLoginFragment.isCancelable = false
            bottomLoginFragment.show(supportFragmentManager, bottomLoginFragment.tag)
        }
    }
}
