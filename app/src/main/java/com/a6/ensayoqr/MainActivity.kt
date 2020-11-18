package com.a6.ensayoqr

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.TokenWatcher
import android.os.WorkSource
import android.text.BoringLayout.make
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.a6.ensayoqr.databinding.ActivityMainBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.lang.Exception
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestCodeCameraPermission = 1001

    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector

    private lateinit var scanResult:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scanResult = binding.scanResult


        val myBitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.qrcode)

        val barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()

        val frame = Frame.Builder().setBitmap(myBitmap).build()

        val detect = barcodeDetector.detect(frame)

        val displayValue = detect.valueAt(0).displayValue
        /*
        if (!checkCameraPermission()) {
            askForCameraPermission()
        } else {
            setupControls()
        }
         */

        Log.d(TAG, displayValue)


    }

    private fun setupControls() {
        detector = BarcodeDetector.Builder(this@MainActivity).build()
        cameraSource = CameraSource.Builder(this@MainActivity, detector)
            .setAutoFocusEnabled(true)
            .build()
        binding.cameraSurfaceView.holder.addCallback(surceCallBack)
        detector.setProcessor(processor)
    }


    private fun checkCameraPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
                )
    }


    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCodeCameraPermission -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        setupControls()
                    } else {
                        Toast.makeText(applicationContext, "Permiso denegado", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                Log.d(TAG, "??")
            }
        }
    }

    private val surceCallBack = object : SurfaceHolder.Callback {

        @SuppressLint("MissingPermission")
        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                cameraSource.start()
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Algo salio mal", Toast.LENGTH_LONG).show()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            cameraSource.stop()
        }

    }

    private val processor = object : Detector.Processor<Barcode> {
        override fun release() {

        }

        override fun receiveDetections(p0: Detector.Detections<Barcode>?) {

            if (p0 != null && p0.detectedItems.isNotEmpty()) {
                val qrCodes: SparseArray<Barcode> = p0.detectedItems
                val code = qrCodes.valueAt(0)
                Log.d(TAG, code.displayValue)
                //scanResult.text = code.displayValue
            } else {
                Log.d(TAG, "Nada")
               // scanResult.text = "nada"
            }
        }

    }


    companion object {
        const val TAG = "TAGG"
    }
}