package com.example.npi_app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.npi_app.RedeemItem
import org.json.JSONObject


class RedeemPointsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redeem_layout)

        // Actualizar indicadores
        val tvSaldo = findViewById<TextView>(R.id.tvSaldo)
        val tvPuntos = findViewById<TextView>(R.id.tvPuntos)
        updateIndicators(tvSaldo, tvPuntos)

        // Cargar ítems
        val items = loadRedeemItems()

        // Configurar RecyclerView
        val rvItems = findViewById<RecyclerView>(R.id.rvItems)
        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.adapter = RedeemItemsAdapter(items) { item ->
            // Acción al hacer clic en un ítem
            onItemClicked(item)
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

    private fun loadRedeemItems(): List<RedeemItem> {
        val inputStream = resources.openRawResource(R.raw.redeem_points)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val itemsArray = jsonObject.getJSONArray("items")

        val items = mutableListOf<RedeemItem>()
        for (i in 0 until itemsArray.length()) {
            val item = itemsArray.getJSONObject(i)

            items.add(
                RedeemItem(
                    id = item.getString("id"),
                    title = item.getString("title"),
                    description = item.getString("description"),
                    cost = item.getInt("cost"),
                    category = item.getString("category"),
                    type = item.getString("type"), // Obligatorio
                    discount = if (item.has("discount")) item.getDouble("discount") else 0.0, // Opcional
                    image = if (item.has("image")) item.getString("image") else null // Opcional
                )
            )
        }
        return items
    }


    private fun onItemClicked(item: RedeemItem) {
        // Aquí puedes navegar a otra actividad para mostrar detalles
        val intent = Intent(this, RedeemDetailsActivity::class.java)
        intent.putExtra("redeemItem", item) // Convertir a HashMap
        startActivity(intent)
    }
}
