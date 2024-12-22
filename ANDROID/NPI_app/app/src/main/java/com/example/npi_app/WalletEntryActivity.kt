package com.example.npi_app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
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



class WalletEntryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wallet_start_layout)
        WalletManager.loadPaymentDb(this)

        // Configurar listeners para cada botón si es necesario
        findViewById<ImageButton>(R.id.btnScanQR).setOnClickListener {
            // Lógica para abrir el escáner QR
            val intent = Intent(this, QRScannerActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnRedeemPoints).setOnClickListener {
            val intent = Intent(this, RedeemPointsActivity::class.java)
            startActivity(intent)
            // Lógica para redimir puntos
        }

        findViewById<ImageButton>(R.id.btnPayments).setOnClickListener {
            // Lógica para mostrar pagos realizados
            val intent = Intent(this, MyPaymentsActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnBonuses).setOnClickListener {
            // Lógica para mostrar bonos adquiridos
            val intent = Intent(this, MyVouchersActivity::class.java)
            startActivity(intent)
        }

        // Indicadores
        val tvSaldo = findViewById<TextView>(R.id.tvSaldo)
        val tvPuntos = findViewById<TextView>(R.id.tvPuntos)

        // Actualizar UI según WalletData
        updateIndicators(tvSaldo, tvPuntos)
    }

    override fun onResume() {
        super.onResume()
        // Actualizar indicadores
        val tvSaldo = findViewById<TextView>(R.id.tvSaldo)
        val tvPuntos = findViewById<TextView>(R.id.tvPuntos)
        updateIndicators(tvSaldo, tvPuntos)
    }

    private fun updateIndicators(tvSaldo: TextView, tvPuntos: TextView) {
        // Actualizar saldo
        tvSaldo.text = getString(R.string.saldo_txt).format(WalletData.saldo)
        if (WalletData.saldo > 0) {
            tvSaldo.setBackgroundColor(Color.parseColor("#1976D2")) // Azul oscuro
        } else {
            tvSaldo.setBackgroundColor(Color.parseColor("#D32F2F")) // Rojo oscuro
        }

        // Actualizar puntos
        tvPuntos.text = getString(R.string.puntos_txt, WalletData.puntos)
        if (WalletData.puntos > 0) {
            tvPuntos.setBackgroundColor(Color.parseColor("#388E3C")) // Verde oscuro
        } else {
            tvPuntos.setBackgroundColor(Color.parseColor("#BDBDBD")) // Gris
        }
    }
}
