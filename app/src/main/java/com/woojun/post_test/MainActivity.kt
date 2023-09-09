package com.woojun.post_test

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_PICK = 100
    private val BASE_URL = "설정"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson : Gson = GsonBuilder()
            .setLenient()
            .create()

        // Retrofit 초기화
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // 이미지 선택 버튼 클릭 리스너 설정
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        // 업로드 버튼 클릭 리스너 설정
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.setOnClickListener {
            // 업로드할 이미지와 설명 텍스트를 서버에 보내는 코드

            val file = File(selectedImagePath) // 선택한 이미지 파일
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("FixImage", file.name, requestFile)
            val descriptionPart = "test테스트"

            val call = apiService.uploadImage(body, descriptionPart)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Log.d("확인", "성공")
                        Log.d("확인", response.body().toString())
                    } else {
                        Log.d("확인", "실패")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("확인", "네트워크 실패")
                    Log.d("확인", t.toString())
                }
            })
        }
    }

    var selectedImagePath: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImageUri!!, projection, null, null, null)
            if (cursor != null) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                selectedImagePath = cursor.getString(columnIndex)
                cursor.close()
            }
        }
    }
}