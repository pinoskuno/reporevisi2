package com.example.storyappsubmission1.Activity.Main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.storyappsubmission1.Activity.Start.WelcomeActivity
import com.example.storyappsubmission1.Data.Functon.Bitmap
import com.example.storyappsubmission1.Data.Functon.CompressImage
import com.example.storyappsubmission1.Data.Functon.Result
import com.example.storyappsubmission1.Data.Functon.uriToFile
import com.example.storyappsubmission1.ViewModel.StoryVM
import com.example.storyappsubmission1.databinding.ActivityStoryBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyViewModel: StoryVM
    private var imageFile: File? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var myLocation: Location? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            else -> {
                binding.locationSwitch.isChecked = false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Gagal Mendapatkan Permsission",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLocation()

        binding.btnTakepciture.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { addStory() }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Failed to chose Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun addStory() {
        if (imageFile != null) {
            storyViewModel.getToken().observe(this){
                token ->if (
                token.isEmpty()
                ){
                    startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
                }
                else{
                val file = CompressImage(imageFile as File)
                val description =
                    binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                val lat = (myLocation?.latitude.takeUnless { it == null } ?: 0.0).toFloat()
                val lon = (myLocation?.longitude.takeUnless { it == null } ?: 0.0).toFloat()


                storyViewModel.addStory( token, imageMultipart, description, lat, lon
                ).observe(this) { response ->
                    when (response) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(
                                this@StoryActivity, response.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Result.Success -> {
                            showLoading(false)
                            intentMain()
                            Toast.makeText(
                                this@StoryActivity, response.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                }
            }
        } else {
            Toast.makeText(
                this@StoryActivity,
                "Failed To Upload",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            imageFile = myFile

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = Bitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.ivStoryPref.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@StoryActivity)
            imageFile = myFile
            binding.ivStoryPref.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.apply {
            visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it!=null){
                LAT = it.latitude.toFloat()
                LON = it.longitude.toFloat()

                Log.d(TAG, "getMyLocation: lat: $LAT, lon: $LON")
            }
        }
    }

    private fun intentMain() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        showLoading(false)
        finish()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                Log.i("Location", location?.latitude.toString())
                if (location != null) {
                    myLocation = location
                } else {
                    Toast.makeText(
                        this@StoryActivity,
                        "Location Not Found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    companion object {
        const val TAG = "StoryActivity"
        const val CAMERA_X_RESULT = 200
        private var LAT = 0.0F
        private var LON = 0.0F
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}