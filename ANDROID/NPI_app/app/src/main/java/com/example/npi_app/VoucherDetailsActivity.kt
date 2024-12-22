package com.example.npi_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.npi_app.ui.theme.NPI_appTheme
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONObject

class VoucherDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voucher_details_layout)

        val tvDetails: TextView = findViewById(R.id.tvVoucherDetails)
        val ivQRCode: ImageView = findViewById(R.id.ivQRCode)
        val btnBack: Button = findViewById(R.id.btnBack)

        // Recibir datos del bono
        val voucherJson = intent.getStringExtra("voucher")
        val voucher = JSONObject(voucherJson ?: "{}")

        // Mostrar detalles
        val details = StringBuilder()
        details.append("Concepto: ${voucher.getString("concept")}\n")
        details.append("Fecha: ${voucher.getString("date")}\n")

        // Claves adicionales
        when (voucher.getString("concept")) {
            "Deportes" -> {
                details.append("Deporte: ${voucher.optString("sport", "N/A")}\n")
                details.append("Lugar: ${voucher.optString("place", "N/A")}\n")
                details.append("Inicio: ${voucher.optString("start", "N/A")}\n")
                details.append("Fin: ${voucher.optString("end", "N/A")}\n")
                details.append("Pista: ${voucher.optString("court", "N/A")}\n")
            }
        }
        tvDetails.text = details.toString()

        // Generar QR
        val qrContent = voucher.toString()
        val qrCode = QRCodeWriter().encode(qrContent, BarcodeFormat.QR_CODE, 1000, 1000)
        val bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.RGB_565)
        for (x in 0 until 1000) {
            for (y in 0 until 1000) {
                bitmap.setPixel(x, y, if (qrCode.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }

        // Dibujar el logo en el centro
        val logo = BitmapFactory.decodeResource(resources, R.drawable.ic_logo_ugr)
        val canvas = Canvas(bitmap)
        val logoWidth = bitmap.width / 5
        val logoHeight = bitmap.height / 5
        val logoResized = Bitmap.createScaledBitmap(logo, logoWidth, logoHeight, false)
        val centerX = (bitmap.width - logoWidth) / 2
        val centerY = (bitmap.height - logoHeight) / 2

        canvas.drawBitmap(logoResized, centerX.toFloat(), centerY.toFloat(), Paint())

        ivQRCode.setImageBitmap(bitmap)

        btnBack.setOnClickListener {
            finish()
        }
    }
}

