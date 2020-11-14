package com.isaacurbna.wallpaperchanger.activity

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.isaacurbna.wallpaperchanger.R
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

	private val pickImage = 100
	private lateinit var button: AppCompatButton
	private lateinit var progressBar: ProgressBar

	@Suppress("BlockingMethodInNonBlockingContext")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		button = findViewById(R.id.button)
		progressBar = findViewById(R.id.progressBar)
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

	@Suppress("BlockingMethodInNonBlockingContext", "DEPRECATION")
	private fun setWallpaper(imageUri: Uri) {
		CoroutineScope(Dispatchers.IO).launch {
			CoroutineScope(Dispatchers.Main).launch {
				progressBar.visibility = View.VISIBLE
				button.isEnabled = false
			}
			val context = this@MainActivity
			val bitmap = cropBitmapFromCenterAndScreenSize(imageUri)
			val manager = WallpaperManager.getInstance(applicationContext)
			val job1 = async {
				try {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
					}
				} catch (ignored: Exception) {
				}
			}
			val job2 = async {
				try {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
					}
				} catch (ignored: Exception) {
				}
			}
			val job3 = async {
				try {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						manager.setBitmap(bitmap)
					}
				} catch (ignored: Exception) {
				}
			}
			awaitAll(job1, job2, job3)
			CoroutineScope(Dispatchers.Main).launch {
				Toast.makeText(context, "Wallpaper set!", Toast.LENGTH_SHORT).show()
				button.isEnabled = true
				progressBar.visibility = View.GONE
			}
		}
	}

	private fun cropBitmapFromCenterAndScreenSize(imageUri: Uri): Bitmap? {
		var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
		val screenWidth: Float
		val screenHeight: Float
		val bitmapWidth = bitmap.width.toFloat()
		val bitmapHeight = bitmap.height.toFloat()
		Log.i("TAG", "bitmap_width $bitmapWidth")
		Log.i("TAG", "bitmap_height $bitmapHeight")
		val displayMetrics = DisplayMetrics()
		val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager)
			.defaultDisplay.getRealMetrics(displayMetrics)
		screenWidth = displayMetrics.widthPixels.toFloat()
		screenHeight = displayMetrics.heightPixels.toFloat()
		Log.i("TAG", "screenWidth $screenWidth")
		Log.i("TAG", "screenHeight $screenHeight")
		val bitmapRatio = (bitmapWidth / bitmapHeight)
		val screenRatio = (screenWidth / screenHeight)
		val bitmapNewWidth: Int
		val bitmapNewHeight: Int
		Log.i("TAG", "bitmap_ratio $bitmapRatio")
		Log.i("TAG", "screen_ratio $screenRatio")
		if (screenRatio > bitmapRatio) {
			bitmapNewWidth = screenWidth.toInt()
			bitmapNewHeight = (bitmapNewWidth / bitmapRatio).toInt()
		} else {
			bitmapNewHeight = screenHeight.toInt()
			bitmapNewWidth = (bitmapNewHeight * bitmapRatio).toInt()
		}
		bitmap = Bitmap.createScaledBitmap(
			bitmap, bitmapNewWidth,
			bitmapNewHeight, true
		)
		Log.i("TAG", "bitmapNewWidth $bitmapNewWidth")
		Log.i("TAG", "bitmapNewHeight $bitmapNewHeight")
		val bitmapGapX: Int
		val bitmapGapY: Int
		bitmapGapX = ((bitmapNewWidth - screenWidth) / 2.0f).toInt()
		bitmapGapY = ((bitmapNewHeight - screenHeight) / 2.0f).toInt()
		Log.i("TAG", "bitmapGapX $bitmapGapX")
		Log.i("TAG", "bitmapGapY $bitmapGapY")
		bitmap = Bitmap.createBitmap(
			bitmap, bitmapGapX, bitmapGapY,
			screenWidth.toInt(), screenHeight.toInt()
		)
		return bitmap
	}
}
