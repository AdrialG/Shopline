package com.adrialg.ecommercecrocodicapp.ui.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.adrialg.ecommercecrocodicapp.R
import com.adrialg.ecommercecrocodicapp.R.string.updating_profile
import com.adrialg.ecommercecrocodicapp.base.BaseActivity
import com.adrialg.ecommercecrocodicapp.data.Session
import com.adrialg.ecommercecrocodicapp.databinding.ActivityEditBinding
import com.adrialg.ecommercecrocodicapp.ui.profile.ProfileActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.DateTimeHelper
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class EditActivity : BaseActivity<ActivityEditBinding, EditViewModel>(R.layout.activity_edit) {

    @Inject
    lateinit var session: Session
    private var filePhoto: File? = null
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.white)

        getUser()
        observe()

        binding.editProfileBack.setOnClickListener {
            onBackPressed()
        }

        binding.profilePicture.setOnClickListener {
            openGallery()
        }

        binding.editProfileValidate.setOnClickListener {
            validateForm()
            openActivity<ProfileActivity> {  }
        }

    }

    private fun getUser() {
        val user = session.getUser()
        binding.user = user
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show(getString(updating_profile))
                            ApiStatus.SUCCESS -> {
                                tos(it.message ?: "Profile Updated")
                                loadingDialog.dismiss()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                            }
                            ApiStatus.EXPIRED -> {

                            }
                            else -> loadingDialog.setResponse(":(")
                        }
                    }
                }
            }
        }
    }

    private fun validateForm() {
        val name = binding.usernameInput.textOf()
        val phone = binding.phoneNumberInput.textOf()


        if (name.isEmpty()) {
            tos("Name can't be empty")
            return
        }

        if (phone.isEmpty()) {
            tos("Phone number can't be empty")
            return
        }

        if (filePhoto == null) {
            if (name == username) {
                tos("Nothing has changed")
                return
            }
            viewModel.updateUser(name, phone)

        } else {
            lifecycleScope.launch {
                val compressesFile = compressFile(filePhoto!!)
                Timber.d("File: $compressesFile")
                if (compressesFile != null) {
                    viewModel.updateUserWPicture(name, phone, compressesFile)
                }
            }
        }
    }

    //MultiPart Gallery
    private var activityLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                generateFileImage(it)
            }
        }

    //Check Gallery Permission
    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    //Open Gallery
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
    }

    //Request Permission Gallery
    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            110
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 200) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openGallery()
            } else {
                Toast.makeText(this, "Gallery Access Denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //Generate Image File
    private fun generateFileImage(uri: Uri) {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()

            val orientation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getOrientation2(uri)
            } else {
                getOrientation(uri)
            }
            val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                createImageFile()
            } else {
                //File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}" + File.separator + "BurgerBangor", getNewFileName())
                File(externalCacheDir?.absolutePath, getNewFileName())
            }

            val fos = FileOutputStream(file)
            var bitmap = image

            if (orientation != -1 && orientation != 0) {

                val matrix = Matrix()
                when (orientation) {
                    6 -> matrix.postRotate(90f)
                    3 -> matrix.postRotate(180f)
                    8 -> matrix.postRotate(270f)
                    else -> matrix.postRotate(orientation.toFloat())
                }
                bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            binding.profilePicture.setImageBitmap(bitmap)
            filePhoto = file
            Timber.d("file : $filePhoto")
        } catch (e: Exception) {
            e.printStackTrace()
            binding.root.snacked("This file can't be used")
        }
    }

    @SuppressLint("Range")
    private fun getOrientation(shareUri: Uri): Int {
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur = contentResolver.query(
            shareUri,
            orientationColumn,
            null,
            null,
            null
        )
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            if (cur.columnCount > 0) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            }
            cur.close()
        }
        return orientation
    }

    @SuppressLint("NewApi")
    private fun getOrientation2(shareUri: Uri): Int {
        val inputStream = contentResolver.openInputStream(shareUri)
        return getOrientation3(inputStream)
    }

    @SuppressLint("NewApi")
    private fun getOrientation3(inputStream: InputStream?): Int {
        val exif: ExifInterface
        var orientation = -1
        inputStream?.let {
            try {
                exif = ExifInterface(it)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return orientation
    }

    //function Create Image
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = DateTimeHelper().createAtLong().toString()
        val storageDir = getAppSpecificAlbumStorageDir(Environment.DIRECTORY_DOCUMENTS, "Attachment")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    //Get Image File Name
    private fun getNewFileName(isPdf: Boolean = false): String {
        val timeStamp = DateTimeHelper().createAtLong().toString()
        return if (isPdf) "PDF_${timeStamp}_.pdf" else "JPEG_${timeStamp}_.jpg"
    }

    //Get Image Album Storage
    private fun getAppSpecificAlbumStorageDir(albumName: String, subAlbumName: String): File {
        val file = File(getExternalFilesDir(albumName), subAlbumName)
        if (!file.mkdirs()) {
        }
        return file
    }


    suspend fun compressFile(filePhoto: File): File? {
        println("Compress 1")
        try {
            println("Compress 2")
            return Compressor.compress(this, filePhoto) {
                resolution(720, 720)
                quality(75)
                format(Bitmap.CompressFormat.JPEG)
                size(514)
            }
        } catch (e: Exception) {
            println("Compress 3")
            tos("Compression Failed")
            e.printStackTrace()
            return null
        }

    }

}