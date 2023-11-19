package com.tegar.submissionaplikasistoryapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.tegar.submissionaplikasistoryapp.R
import com.tegar.submissionaplikasistoryapp.data.remote.ResultMessage
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseAdd
import com.tegar.submissionaplikasistoryapp.databinding.ActivityAddStoryBinding
import com.tegar.submissionaplikasistoryapp.ui.viewmodel.AddStoryViewModel
import com.tegar.submissionaplikasistoryapp.ui.viewmodel.MainViewModelFactory
import com.tegar.submissionaplikasistoryapp.util.ToastUtils.showToast
import com.tegar.submissionaplikasistoryapp.util.getImageUri
import com.tegar.submissionaplikasistoryapp.util.reduceFileImage
import com.tegar.submissionaplikasistoryapp.util.uriToFile

class AddStoryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var activityAddStoryBinding: ActivityAddStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        MainViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            val message =
                if (isGranted) getString(R.string.permission_request_granted) else getString(
                    R.string.permission_request_denied
                )
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

    private val requestPermissionLauncherLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            val message =
                if (isGranted) getString(R.string.permission_request_granted) else getString(
                    R.string.permission_request_denied
                )
            if (!isGranted) {
                activityAddStoryBinding.switchLocation.isChecked = false
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(activityAddStoryBinding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        activityAddStoryBinding.btnGaleri.setOnClickListener(this)
        activityAddStoryBinding.btnCamera.setOnClickListener(this)

        if (savedInstanceState != null) {
            @Suppress("DEPRECATION")
            currentImageUri = savedInstanceState.getParcelable("currentImageUri")
            addStoryViewModel.setSelectedImageUri(currentImageUri)
        }

        addStoryViewModel.selectedImageUri.observe(this) { uri ->
            currentImageUri = uri
            showImage()
        }

        activityAddStoryBinding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!allPermissionsGrantedLocation()) {
                    requestPermissionLauncherLocation.launch(REQUIRED_PERMISSION_LOCATION)
                }
                getSingleUpdateLocation { latitude, longitude ->
                    addStoryViewModel.lat = latitude
                    addStoryViewModel.lon = longitude
                }
            } else {
                addStoryViewModel.lat = null
                addStoryViewModel.lon = null
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("currentImageUri", currentImageUri)
    }

    @SuppressLint("DiscouragedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu_add, menu)
        supportActionBar?.title = getString(R.string.new_post)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_post -> sendStory()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_galeri -> startGallery()
            R.id.btn_camera -> startCamera()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun allPermissionsGrantedLocation() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun showImage() {
        currentImageUri?.let {
            activityAddStoryBinding.photo.setImageURI(it)
        }
    }

    private fun sendStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = activityAddStoryBinding.desc.text.toString()

            if (description.isEmpty()) {
                showToast(this, getString(R.string.description_empty))
            } else {
                val latitude = addStoryViewModel.lat?.toFloat()
                val longitude = addStoryViewModel.lon?.toFloat()
                addStoryViewModel.newPost(description, imageFile, latitude, longitude)
                    .observe(this) { result ->
                        handleResult(result)
                    }
            }
        } ?: showToast(this, getString(R.string.empty_image_warning))
    }

    private fun showLoading(isLoading: Boolean) {
        activityAddStoryBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleResult(result: ResultMessage<ResponseAdd>) {
        when (result) {
            is ResultMessage.Loading -> showLoading(true)
            is ResultMessage.Success -> {
                showToast(this, getString(R.string.success, result.data.message.toString()))
                showLoading(false)
                val resultIntent = Intent()
                resultIntent.putExtra("story_added", true)
                setResult(RESULT_OK, resultIntent)
                finish()
            }

            is ResultMessage.Error -> {
                val exception = result.exception
                val errorMessage =
                    exception.message ?: getString(R.string.an_error_occurred_during_post_creation)
                showToast(this, errorMessage)
                showLoading(false)
            }
        }
    }

    private fun getSingleUpdateLocation(callback: (Double, Double) -> Unit) {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            activityAddStoryBinding.switchLocation.isChecked = false
            showToast(this, getString(R.string.please_enable_location))
        } else {
            val currentLocationRequest = CurrentLocationRequest.Builder()
                .setMaxUpdateAgeMillis(TIME_MAX_UPDATE)
                .setPriority(PRIORITY_HIGH_ACCURACY)
                .build()

            val cancellationTokenSource = CancellationTokenSource()

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.getCurrentLocation(
                currentLocationRequest,
                cancellationTokenSource.token
            )
                .addOnSuccessListener { location ->
                    if (location != null) {
                        callback(location.latitude, location.longitude)
                    }
                }
        }
    }

    companion object {
        private const val TIME_MAX_UPDATE = 10000L
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    }
}
