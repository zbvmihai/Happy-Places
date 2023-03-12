package com.zabava.happyplaces.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zabava.happyplaces.database.DatabaseHandler
import com.zabava.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.zabava.happyplaces.models.HappyPlaceModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AddHappyPlaceActivity : AppCompatActivity() {

    private var binding: ActivityAddHappyPlaceBinding? = null

    private val cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.tbAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding?.tbAddPlace?.setNavigationOnClickListener {
            finish()
        }

        setDate()
        addPhoto()

        binding?.btnSave?.setOnClickListener {

            if (binding?.etTitle?.text?.isEmpty()!!) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
            }else if (binding?.etDescription?.text?.isEmpty()!!) {
                Toast.makeText(this, "Please enter a Description", Toast.LENGTH_SHORT).show()
            } else if (binding?.etDate?.text?.isEmpty()!!) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            }else if (binding?.etLocation?.text?.isEmpty()!!) {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
            }else if (saveImageToInternalStorage == null){
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }else{
                val happyPlaceModel = HappyPlaceModel(
                    0,
                    binding?.etTitle?.text.toString(),
                    saveImageToInternalStorage.toString(),
                    binding?.etDescription?.text.toString(),
                    binding?.etDate?.text.toString(),
                    binding?.etLocation?.text.toString(),
                    mLatitude,
                    mLongitude)
                val dbHandler = DatabaseHandler(this)
                val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)

                if (addHappyPlace > 0){
                    Toast.makeText(this, "The details are inserted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun addPhoto() {
        binding?.tvAddImage?.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this@AddHappyPlaceActivity)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems =
                arrayOf("Select photo from Gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems) { _, which ->
                when (which) {
                    0 -> choosePhotoFromGallery()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }
    }

    private fun takePhotoFromCamera() {
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(
                report: MultiplePermissionsReport?
            ) {
                if (report!!.areAllPermissionsGranted()) {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA)
                } else {
                    Toast.makeText(
                        this@AddHappyPlaceActivity,
                        "Permissions denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(
                report: MultiplePermissionsReport?
            ) {
                if (report!!.areAllPermissionsGranted()) {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(galleryIntent, GALLERY)
                } else {
                    Toast.makeText(
                        this@AddHappyPlaceActivity,
                        "Permissions denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage(
                "It looks like you have turned off permissions required for this feature." +
                        " It can be enabled under application settings."
            )
            .setPositiveButton("Go to settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun setDate() {
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            binding?.etDate?.setText(sdf.format(cal.time).toString())
        }

        binding?.etDate?.setOnClickListener {
            DatePickerDialog(
                this@AddHappyPlaceActivity,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun saveImageToInternalStorage(image: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media
                            .getBitmap(this.contentResolver, contentURI)

                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        binding?.ivPlaceImage?.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddHappyPlaceActivity,
                            "Failed to load the Image!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else if (requestCode == CAMERA) {
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                binding?.ivPlaceImage?.setImageBitmap(thumbnail)
                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
            }
        }
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"


    }
}

