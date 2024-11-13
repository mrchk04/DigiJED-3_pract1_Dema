package com.example.digijed_3pract1dema

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.digijed_3pract1dema.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var view:ActivityMainBinding
    private var pic:Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), 11)
        }

        view.takePic.setOnClickListener({ openCamera() })
        view.sendPic.setOnClickListener({ shareImage() })
    }

    private fun openCamera(){
        // Відкриваємо камеру
        val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicIntent, 10)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            pic = extras?.get("data") as Bitmap?
            pic?.let {
                view.photoView.setImageBitmap(it)
            }
        } else {
            showToast("Помилка")
        }
    }

    private fun shareImage(){
        pic?.let { image ->
            // Зберігаємо зображення у сховище і отримуємо URI
            val path = MediaStore.Images.Media.insertImage(contentResolver, image, "Selfie", null)
            val imageUri = Uri.parse(path)

            // Відкриваємо поштовий додаток
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_SUBJECT, "DigiJED Dema Mariia")
            }
            startActivity(Intent.createChooser(emailIntent, "Відіслати селфі"))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}