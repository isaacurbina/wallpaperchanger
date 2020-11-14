package com.isaacurbna.wallpaperchanger.activity

import android.app.WallpaperManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.isaacurbna.wallpaperchanger.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

	private val pickImage = 100

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		button.setOnClickListener {
			val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
			startActivityForResult(gallery, pickImage)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == RESULT_OK && requestCode == pickImage) {
			data?.data?.let {
				setWallpaper(it)
			}
		}
	}

	private fun setWallpaper(imageUri: Uri) {
		val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
		//val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
		val manager = WallpaperManager.getInstance(applicationContext)
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
			}
			Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show()
		} catch (e: IOException) {
			Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
		}
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
			}
			Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show()
		} catch (e: IOException) {
			Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
		}
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				manager.setBitmap(bitmap)
			}
			Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show()
		} catch (e: IOException) {
			Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
		}
	}
}
