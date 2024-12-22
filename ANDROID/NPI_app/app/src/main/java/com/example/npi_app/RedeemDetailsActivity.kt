package com.example.npi_app

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.npi_app.RedeemItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RedeemDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redeem_details_layout)

        // Obtener vistas
        val tvSaldo = findViewById<TextView>(R.id.tvSaldo)
        val tvPuntos = findViewById<TextView>(R.id.tvPuntos)
        val ivRedeemImage = findViewById<ImageView>(R.id.ivRedeemImage)
        val tvRedeemTitle = findViewById<TextView>(R.id.tvRedeemTitle)
        val tvRedeemDescription = findViewById<TextView>(R.id.tvRedeemDescription)
        val btnRedeem = findViewById<Button>(R.id.btnRedeem)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        // Obtener datos del ítem
        val redeemItem = intent.getParcelableExtra<RedeemItem>("redeemItem")!! as RedeemItem

        // Actualizar indicadores
        updateIndicators(tvSaldo, tvPuntos)

        // Mostrar detalles del ítem
        tvRedeemTitle.text = redeemItem.title
        tvRedeemDescription.text = redeemItem.description

        // Cargar imagen si existe
        redeemItem.image?.let {
            val resId = resources.getIdentifier(it, "drawable", packageName)
            if (resId != 0) {
                ivRedeemImage.setImageResource(resId)
                ivRedeemImage.visibility = View.VISIBLE
            }
        }

        // Botón Canjear
        btnRedeem.text = getString(R.string.redeem_button_text, redeemItem.cost)
        btnRedeem.setOnClickListener {
            if (WalletData.puntos >= redeemItem.cost) {

                AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_redeem_title)
                    .setMessage(getString(R.string.confirm_redeem_message, redeemItem.cost))
                    .setPositiveButton(R.string.yes_button) { _, _ ->
                        WalletData.puntos -= redeemItem.cost
                        updateIndicators(tvSaldo, tvPuntos)

                        if (redeemItem.type == "discount") {
                            WalletData.discounts.add(
                                mapOf(
                                    "title" to redeemItem.title,
                                    "discount" to redeemItem.discount,
                                    "category" to redeemItem.category
                                )
                            )
                        }

                        recordRedemption(
                            mapOf(
                                "title" to redeemItem.title,
                                "cost" to redeemItem.cost,
                                "type" to redeemItem.type,
                                "category" to redeemItem.category,
                            ),
                            redeemItem.cost
                        )

                        Toast.makeText(this, "Canjeo completado", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, RedeemConfirmationActivity::class.java)
                        intent.putExtra("redeem_type", redeemItem.type)
                        startActivity(intent)
                        //finish() // Regresar a la actividad anterior
                    }
                    .setNegativeButton(R.string.no_button, null)
                    .show()
            } else {
                Toast.makeText(this, "Puntos insuficientes", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Cancelar
        btnCancel.setOnClickListener {
            finish() // Regresar a la actividad anterior
        }
    }

    private fun updateIndicators(tvSaldo: TextView, tvPuntos: TextView) {
        tvSaldo.text = getString(R.string.saldo_txt).format(WalletData.saldo)
        tvSaldo.setBackgroundColor(
            if (WalletData.saldo > 0) Color.parseColor("#1976D2") else Color.parseColor("#D32F2F")
        )

        tvPuntos.text = getString(R.string.puntos_txt, WalletData.puntos)
        tvPuntos.setBackgroundColor(
            if (WalletData.puntos > 0) Color.parseColor("#388E3C") else Color.parseColor("#BDBDBD")
        )
    }

    private fun recordRedemption(data: Map<String, Any>, points: Int) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val transaction = mapOf(
            "data" to data,
            "method" to "points",
            "points" to points,
            "date" to currentDate
        )
        WalletData.transactions.add(transaction)
    }
}
