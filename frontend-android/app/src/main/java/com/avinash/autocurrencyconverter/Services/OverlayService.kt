package com.avinash.autocurrencyconverter.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.avinash.autocurrencyconverter.Components.SelectionView
import com.avinash.autocurrencyconverter.CurrencyConverter
import com.avinash.autocurrencyconverter.CurrencyExtractor
import com.avinash.autocurrencyconverter.CurrencyMatch
import com.avinash.autocurrencyconverter.R
import com.avinash.autocurrencyconverter.Utils
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.properties.Delegates


class OverlayService : Service() {

    private lateinit var windowManager: WindowManager

    private var resultCode by Delegates.notNull<Int>()
    private lateinit var data: Intent

    private var selectionView: SelectionView? = null

    private lateinit var mediaProjectionManager: MediaProjectionManager
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        resultCode = intent?.getIntExtra("resultCode", 1) ?: 1
        data = intent?.getParcelableExtra("data") ?: Intent()
        val notificationChannelId = "overlay_service_channel"
        val channel = NotificationChannel(
            notificationChannelId, "Overlay Service", NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Overlay Active").setContentText("Listening to clipboard…")
            .setSmallIcon(R.drawable.ic_launcher_foreground).build()

        startForeground(1, notification)

        mediaProjectionManager = getSystemService(MediaProjectionManager::class.java)
        projection = mediaProjectionManager.getMediaProjection(resultCode, data)

        ContextCompat.registerReceiver(applicationContext, object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                showSelectionOverlay()
            }
        }, IntentFilter("broadcast_tile"), ContextCompat.RECEIVER_NOT_EXPORTED)
        return super.onStartCommand(intent, flags, startId)
    }


    private var projection: MediaProjection? = null

    private fun showSelectionOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        selectionView = SelectionView(applicationContext) { rect ->
            captureAndCrop(rect)
            windowManager.removeView(selectionView)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(selectionView, params)
    }


    private fun captureAndCrop(rect: Rect) {
        val metrics = Resources.getSystem().displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        var virtualDisplay = projection?.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            null
        )

        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener

            val bitmap = imageToBitmap(image)
            image.close()

            val cropped =
                Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())

            saveBitmap(cropped)

            reader.setOnImageAvailableListener(null, null)
            imageReader.close()
            virtualDisplay?.release()
            virtualDisplay = null

        }, Handler(Looper.getMainLooper()))
    }

    private fun imageToBitmap(image: Image): Bitmap {
        val plane = image.planes[0]
        val buffer = plane.buffer
        val pixelStride = plane.pixelStride
        val rowStride = plane.rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = createBitmap(image.width + rowPadding / pixelStride, image.height)
        bitmap.copyPixelsFromBuffer(buffer)
        return Bitmap.createBitmap(bitmap, 0, 0, image.width, image.height)
    }

    private fun saveBitmap(bitmap: Bitmap) {
        val file = File(getExternalFilesDir(null), "cropped.png")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        parseImage(file)
    }


    private fun parseImage(file: File) {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val image = InputImage.fromBitmap(bitmap, 0)
        textRecognizer.process(image).addOnSuccessListener { result ->
            result.textBlocks.forEach { s ->
                CurrencyExtractor.extract(s.text).forEach { match ->
                    Log.e("Curr",s.text+"   "+match.currencyCode+match.amount)
                    toast(
                        "Converted ${match.currencyCode} ${match.amount} = ${
                            CurrencyConverter.getDefaultCurrency(
                                applicationContext
                            )
                        } ${
                            CurrencyConverter.convert(
                                Utils.getSharedPrefs(applicationContext),
                                match.amount.toFloat(),
                                match.currencyCode,
                                CurrencyConverter.getDefaultCurrency(applicationContext)
                            )
                        }"
                    )
                }
            }
        }.addOnFailureListener {
            toast("Failed to parse Image, Please Try Again")
        }
    }

    private fun toast(message: String) =
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
