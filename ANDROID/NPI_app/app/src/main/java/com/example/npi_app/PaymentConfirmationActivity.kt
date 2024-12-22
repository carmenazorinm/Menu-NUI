package com.example.npi_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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

class PaymentConfirmationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_confirmed_layout)

        // Obtener datos del intent
        val price = intent.getDoubleExtra("price", 0.0)
        val voucher = intent.getBooleanExtra("voucher", false)

        // Mostrar puntos ganados
        val pointsEarned = (price * 10).toInt()
        WalletData.puntos += pointsEarned

        val tvPointsMessage = findViewById<TextView>(R.id.tvPointsMessage)
        tvPointsMessage.text = getString(R.string.points_message, pointsEarned)

        // Mostrar mensaje de bono si aplica
        val tvVoucherMessage = findViewById<TextView>(R.id.tvVoucherMessage)
        if (voucher) {
            tvVoucherMessage.visibility = View.VISIBLE
        }

        // Configurar el botón de salir
        val btnExit = findViewById<Button>(R.id.btnExit)
        btnExit.setOnClickListener {
            val intent = Intent(this, WalletEntryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
