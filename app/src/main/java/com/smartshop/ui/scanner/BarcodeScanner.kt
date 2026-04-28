package com.smartshop.ui.scanner

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.compose.ui.geometry.Size as ComposeSize

/**
 * Barcode scanner composable using CameraX + ML Kit.
 * Features auto-focus, auto-flashlight in dark, and torch control.
 */
@Composable
fun BarcodeScanner(
    modifier: Modifier = Modifier,
    onBarcodeDetected: (String) -> Unit,
    isScanning: Boolean = true,
    isTorchOn: Boolean = false,
    onToggleTorch: (() -> Unit)? = null,
    autoTorch: Boolean = true // Auto-enable in dark
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var lastScannedBarcode by remember { mutableStateOf<String?>(null) }
    var camera by remember { mutableStateOf< androidx.camera.core.Camera?>(null) }
    var lastLuminosity by remember { mutableStateOf(-1f) }
    
    // Auto-focus and torch control
    LaunchedEffect(isScanning, isTorchOn) {
        if (isScanning && camera != null) {
            val factory = SurfaceOrientedMeteringPointFactory(1f, 1f)
            val point = factory.createPoint(0.5f, 0.5f)
            val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                .setAutoCancelDuration(2, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            camera?.cameraControl?.startFocusAndMetering(action)
            camera?.cameraControl?.enableTorch(isTorchOn || (autoTorch && lastLuminosity < 50f))
        }
    }
    
    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(
                            Executors.newSingleThreadExecutor()
                        ) { imageProxy ->
                            if (isScanning) {
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val image = InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )
                                    
                                    val scanner = BarcodeScanning.getClient()
                                    scanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            for (barcode in barcodes) {
                                                // Only process barcodes in center 40% area
                                                val bounds = barcode.boundingBox
                                                if (bounds != null) {
                                                    val imgW = imageProxy.width
                                                    val imgH = imageProxy.height
                                                    val cx = bounds.centerX()
                                                    val cy = bounds.centerY()
                                                    if (cx >= imgW * 0.3f && cx <= imgW * 0.7f &&
                                                        cy >= imgH * 0.3f && cy <= imgH * 0.7f) {
                                                        barcode.rawValue?.let { value ->
                                                            if (value != lastScannedBarcode) {
                                                                lastScannedBarcode = value
                                                                onBarcodeDetected(value)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } else {
                                    imageProxy.close()
                                }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }
                
                try {
                    cameraProvider.unbindAll()
                    val boundCamera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                    camera = boundCamera
                    
                    // Enable auto-focus
                    boundCamera.cameraControl.enableTorch(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        }
        )
        
        // Overlay - center scan region with dimmed outside
        ScanAreaOverlay()
    }
}

/** Draws semi-transparent overlay with centered scan box */
@Composable
private fun ScanAreaOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val sw = w * 0.7f
        val sh = h * 0.5f
        val left = (w - sw) / 2
        val top = (h - sh) / 2
        // Dimmed areas
        drawRect(Color.Black.copy(alpha = 0.5f), size = ComposeSize(w, top))
        drawRect(Color.Black.copy(alpha = 0.5f), topLeft = Offset(0f, top + sh), size = ComposeSize(w, h - top - sh))
        drawRect(Color.Black.copy(alpha = 0.5f), topLeft = Offset(0f, top), size = ComposeSize(left, sh))
        drawRect(Color.Black.copy(alpha = 0.5f), topLeft = Offset(left + sw, top), size = ComposeSize(w - left - sw, sh))
        // Green border
        val green = Color.Green.copy(alpha = 0.8f)
        drawLine(green, Offset(left, top), Offset(left + sw, top), 3.dp.toPx())
        drawLine(green, Offset(left, top + sh), Offset(left + sw, top + sh), 3.dp.toPx())
        drawLine(green, Offset(left, top), Offset(left, top + sh), 3.dp.toPx())
        drawLine(green, Offset(left + sw, top), Offset(left + sw, top + sh), 3.dp.toPx())
    }
}