package com.example.npi_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRScannerActivity : BaseActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_scanner_layout)

        // Cargar la base de datos de pagos
        //paymentDb = loadPaymentDB()

        previewView = findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Solicitar permisos de cámara
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val analysisUseCase = androidx.camera.core.ImageAnalysis.Builder()
                .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysisUseCase.setAnalyzer(cameraExecutor, { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    val scanner = BarcodeScanning.getClient()

                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                barcode.rawValue?.let { qrCodeValue ->
                                    imageProxy.close()
                                    scanner.close()
                                    handleQRCode(qrCodeValue)
                                    return@addOnSuccessListener
                                }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al escanear QR", Toast.LENGTH_SHORT).show()
                        }.addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            })

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysisUseCase)
            } catch (exc: Exception) {
                Toast.makeText(this, "Error al inicializar la cámara", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    //private lateinit var paymentDb: Map<String, Map<String, Any>>

    private fun loadPaymentDB(): Map<String, Map<String, Any>> {
        val rawResource = resources.openRawResource(R.raw.payment_db)
        return try {
            val jsonString = rawResource.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            // Convertir el JSON a un mapa
            jsonObject.keys().asSequence().associateWith { key ->
                val valueObject = jsonObject.getJSONObject(key)
                valueObject.keys().asSequence().associateWith { valueKey ->
                    valueObject.get(valueKey)
                }
            }
        } catch (e: JSONException) {
            Toast.makeText(this, "Error al cargar la base de datos de pagos", Toast.LENGTH_LONG).show()
            emptyMap()
        } catch (e: Exception) {
            Toast.makeText(this, "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
            emptyMap()
        } finally {
            rawResource.close()
        }
    }

    private var isProcessingQR = false

    private fun handleQRCode(qrContent: String) {
        if (isProcessingQR) return
        isProcessingQR = true

        Toast.makeText(this, "QR escaneado: $qrContent", Toast.LENGTH_SHORT).show()
        try {
            // Intenta convertir el contenido del QR a un JSONObject
            val jsonObject = JSONObject(qrContent)

            // Comprueba si contiene la clave "token"
            if (!jsonObject.has("token")) {
                Toast.makeText(this, "QR no válido: contenido no reconocido", Toast.LENGTH_SHORT).show()
                isProcessingQR = false
                return
            }

            // Extrae el valor de "token"
            val token = jsonObject.getString("token")

            // Comprueba si el token existe en payment_db
            if (!WalletManager.existsPayment(token)) {
                Toast.makeText(this, "QR no válido: el pago no existe", Toast.LENGTH_SHORT).show()
                isProcessingQR = false
                return
            }

            if(!WalletManager.isPayable(token)) {
                Toast.makeText(
                    this,
                    "QR no válido: este pago ya ha sido realizado",
                    Toast.LENGTH_SHORT
                ).show()
                isProcessingQR = false
                return
            }

            val paymentData = WalletManager.getPaymentData(token)
            if (paymentData!!["multipay"] == false && (paymentData["user_id"] != WalletData.userId && paymentData["user_id"] != "any")) {
                Toast.makeText(
                    this,
                    "QR no válido: el pago no corresponde a este usuario",
                    Toast.LENGTH_SHORT
                ).show()
                isProcessingQR = false
                return
            }

            // Token válido: Continuar al siguiente paso
            Toast.makeText(this, "QR válido. Token: $token", Toast.LENGTH_SHORT).show()

            // Pasar a la siguiente fase (puedes reemplazar esta línea según lo que quieras hacer)
            goToPaymentDetails(token)

        } catch (e: Exception) {
            // Si no se puede interpretar como JSON, el QR no es válido
            Toast.makeText(this, "QR no válido: contenido no reconocido", Toast.LENGTH_SHORT).show()
            isProcessingQR = false
            //MAke a toast with the exception message
            //Toast.makeText(this, "QR no válido: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToPaymentDetails(token: String) {
        // Aquí implementarás el siguiente paso.
        Toast.makeText(this, "Procesando token: $token", Toast.LENGTH_SHORT).show()
        // Por ejemplo, podrías iniciar otra actividad para mostrar los detalles del pago.
        val paymentData = WalletManager.getPaymentData(token)
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("paymentData", paymentData?.let { HashMap(it) }) // Convertir a HashMap
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        isProcessingQR = false // Permitir que se lean nuevos QR al volver a esta actividad
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}