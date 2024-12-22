package com.example.npi_app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.npi_app.ui.theme.NPI_appTheme

class ApplyDiscountsActivity : BaseActivity() {

    private var originalPrice: Double = 0.0
    private var appliedDiscounts: MutableList<Map<String, Any>> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apply_discount_layout)

        val tvSaldo: TextView = findViewById(R.id.tvSaldo)
        val tvPuntos: TextView = findViewById(R.id.tvPuntos)
        val tvPrice: TextView = findViewById(R.id.tvOriginalPrice)
        val tvDiscounted: TextView = findViewById(R.id.tvDiscountedPrice)
        val tvFinalPrice: TextView = findViewById(R.id.tvFinalPrice)
        val rvDiscounts: RecyclerView = findViewById(R.id.rvDiscounts)
        val btnApply: Button = findViewById(R.id.btnApplyDiscounts)

        updateIndicators(tvSaldo, tvPuntos)

        // Obtener precio original y categoría
        originalPrice = intent.getDoubleExtra("price", 0.0)
        val category = intent.getStringExtra("category") ?: "General"

        tvPrice.text = getString(R.string.original_price, originalPrice)

        // Filtrar descuentos según categoría
        val categoryDiscounts = WalletData.discounts.filter { it["category"] == category }

        // Configurar ListView

        // Configurar el RecyclerView
        val adapter = DiscountsAdapter(categoryDiscounts, originalPrice, ) { selectedItems ->
            appliedDiscounts.clear()
            appliedDiscounts.addAll(selectedItems)
            updatePrices(tvDiscounted, tvFinalPrice)
        }
        rvDiscounts.adapter = adapter
        rvDiscounts.layoutManager = LinearLayoutManager(this)


        // Aplicar descuentos y devolver resultado
        btnApply.setOnClickListener {
            val intent = Intent()
            val totalDiscount = appliedDiscounts.sumOf { it["discount"] as Double }
            val truncatedDiscount = minOf(totalDiscount, originalPrice)
            intent.putExtra("discount_amount", truncatedDiscount)

            val selectedIndices = appliedDiscounts.map { WalletData.discounts.indexOf(it) }
            intent.putIntegerArrayListExtra("selected_indices", ArrayList(selectedIndices))

            setResult(RESULT_OK, intent)
            finish()
        }


    }

    private fun updatePrices(tvDiscounted: TextView, tvFinalPrice: TextView) {
        val totalDiscount = appliedDiscounts.sumOf { it["discount"] as Double }
        val truncatedDiscount = minOf(totalDiscount, originalPrice)
        val finalPrice = originalPrice - truncatedDiscount

        tvDiscounted.text = getString(R.string.discount_applied, truncatedDiscount)
        tvFinalPrice.text = getString(R.string.final_price, finalPrice)
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


}
