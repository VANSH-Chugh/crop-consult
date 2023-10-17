package com.example.thecropconsultapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MainActivity : AppCompatActivity() {
    private val BASE_URL = "https:127.0.0.1:5000" // The base url for api request
    private val PICK_IMAGE_REQUEST = 1
    private val captureImageRequestCode = 1001
    private lateinit var selectedImageView: ImageView
    private lateinit var textView: TextView

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // Replace with your API base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectedImageView = findViewById(R.id.selectedImageView)
        textView = findViewById(R.id.textView)
        val selectImageFromGalleryButton = findViewById<Button>(R.id.selectImageFromGalleryButton)
        val captureImageButton = findViewById<Button>(R.id.captureImageButton)

        selectImageFromGalleryButton.setOnClickListener {
            openGallery()
        }


    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                selectedImageView.setImageURI(imageUri)
                // You have the selected image URI, now upload it
                uploadImage(imageUri)
            }
        }
    }


    private fun uploadImage(imageUri: Uri) {
        val apiService = retrofit.create(ApiService::class.java)

        val imageFile = imageUri.path?.let { File(it) }
        val requestFile =
            imageFile?.let { RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it) }
        val imagePart =
            requestFile?.let { MultipartBody.Part.createFormData("image", imageFile.name, it) }

        imagePart?.let { apiService.uploadImage(it) }?.enqueue(object : Callback<JsonResponse> {
            override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                if (response.isSuccessful) {
                    val responseModel = response.body()
                    // Handle the response here
                    if (responseModel != null) {
                        // Access and use the data in the response model
                        val data = responseModel.disease
                        // Do something with 'data'
                        textView.text = data
                    }

                } else {
                    // Handle the error'
                    // Handle non-successful response (HTTP status code is not 2xx)
                    val errorMessage = "API call not successful: ${response.code()}"
                    // Handle 'errorMessage' appropriately
                    Toast.makeText(this@MainActivity, "$errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                // Handle the network failure
            }
        })
    }
}
