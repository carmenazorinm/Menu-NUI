package com.example.npi_app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.npi_app.ui.theme.NPI_appTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentActivity : BaseActivity() {

    private val APPLY_DISCOUNTS_REQUEST_CODE = 1001

    private var appliedDiscountAmount: Double = 0.0
    private var selectedDiscounts = mutableListOf<Int>()
    private lateinit var paymentData: Map<String, Any>
    private lateinit var applyDiscountsLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_layout)

        val tvSaldo = findViewById<TextView>(R.id.tvSaldo)
        val tvPuntos = findViewById<TextView>(R.id.tvPuntos)
        val paymentInfoContainer = findViewById<LinearLayout>(R.id.paymentInfoContainer)

        val btnPagar = findViewById<Button>(R.id.btnPagar)
        val btnCanjear = findViewById<Button>(R.id.btnCanjear)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        // Actualizar indicadores de saldo y puntos
        updateIndicators(tvSaldo, tvPuntos)

        // Cargar la información del pago
        paymentData = (intent.getSerializableExtra("paymentData") as? Map<String, Any>)!!
        displayPaymentInfo(paymentInfoContainer, paymentData)

        // Acciones de los botones
        /*
        btnPagar.setOnClickListener {
            // Implementar lógica para pagar
        }*/
        setupPayButton(btnPagar, paymentData["price"] as Double - appliedDiscountAmount as Double, tvSaldo, tvPuntos)

        btnCanjear.setOnClickListener {
            // Implementar lógica para canjear descuentos
            val intent = Intent(this, ApplyDiscountsActivity::class.java)
            intent.putExtra("price", paymentData["price"] as Double)
            intent.putExtra("category", paymentData["concept"] as String)
            //startActivity(intent)
            applyDiscountsLauncher.launch(intent)
        }

        btnCancelar.setOnClickListener {
            // Regresar a la actividad de entrada
            finish()
        }

        applyDiscountsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                appliedDiscountAmount = data?.getDoubleExtra("discount_amount", 0.0) ?: 0.0
                val selectedIndices = data?.getIntegerArrayListExtra("selected_indices") ?: arrayListOf()

                selectedDiscounts.clear()
                selectedDiscounts.addAll(selectedIndices)

                val discountedPrice = paymentData["price"] as Double - appliedDiscountAmount

                setupPayButton(findViewById(R.id.btnPagar), discountedPrice, findViewById(R.id.tvSaldo), findViewById(R.id.tvPuntos))
            }
        }

    }

    /*
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APPLY_DISCOUNTS_REQUEST_CODE && resultCode == RESULT_OK) {
            appliedDiscountAmount = data?.getDoubleExtra("discount_amount", 0.0) ?: 0.0
            val selectedIndices = data?.getIntegerArrayListExtra("selected_indices") ?: arrayListOf()

            selectedDiscounts.clear()
            selectedDiscounts.addAll(selectedIndices)

            val discountedPrice = paymentData?.get("price") as Double - appliedDiscountAmount

            setupPayButton(findViewById(R.id.btnPagar), discountedPrice, findViewById(R.id.tvSaldo), findViewById(R.id.tvPuntos))
        }
    }*/


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

    private fun displayPaymentInfo(container: LinearLayout, data: Map<String, Any>) {
        container.removeAllViews()

        // List of keys to exclude from display
        val excludedKeys = setOf("type", "concept", "user", "user_id", "status", "payed_by", "multipay", "token", "voucher", "access_token")

        // Display "concept" first if it exists
        val concept = data["concept"]
        if (concept != null) {
            val conceptTextView = TextView(this).apply {
                text = getString(R.string.concept_label, concept) // String resource for "Concept"
                textSize = 20f
                setTextColor(Color.BLACK)
                setPadding(8, 8, 8, 8)
            }
            container.addView(conceptTextView)
        }

        // Display other allowed keys
        data.filterKeys { key -> !excludedKeys.contains(key) }.forEach { (key, value) ->
            val localizedKey = getLocalizedKey(key) // Fetch localized string for the key
            val textView = TextView(this).apply {
                text = getString(R.string.key_value_format, localizedKey, value) // e.g., "Name: John"
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(8, 8, 8, 8)
            }
            container.addView(textView)
        }
        /*
        data.forEach { (key, value) ->
            val textView = TextView(this).apply {
                text = "$key: $value"
                textSize = if (key == "concept") 20f else 16f
                setTextColor(Color.BLACK)
                setPadding(8, 8, 8, 8)
            }
            container.addView(textView)
        }*/
    }

    private fun getLocalizedKey(key: String): String {
        return when (key) {
            "price" -> getString(R.string.price_label)
            "date" -> getString(R.string.date_label)
            "sport" -> getString(R.string.sport_label)
            "place" -> getString(R.string.place_label)
            "court" -> getString(R.string.court_label)
            "start" -> getString(R.string.start_time_label)
            "end" -> getString(R.string.end_time_label)
            "degree" -> getString(R.string.degree_label)
            "course" -> getString(R.string.course_label)
            // Add other keys as needed
            else -> key // Fallback to the raw key if no resource is found
        }
    }

    private fun setupPayButton(
        button: Button,
        price: Double,
        tvSaldo: TextView,
        tvPuntos: TextView
    ) {
        // Establecer el texto del botón con el precio
        button.text = getString(R.string.pay_button_text, price)

        // Configurar la acción del botón
        button.setOnClickListener {
            if (WalletData.saldo < price) {
                // Mostrar alerta de saldo insuficiente
                AlertDialog.Builder(this)
                    .setTitle(R.string.alert_title_insufficient_balance)
                    .setMessage(R.string.alert_message_insufficient_balance)
                    .setPositiveButton(R.string.ok_button, null)
                    .show()
            } else {
                // Mostrar alerta de confirmación
                AlertDialog.Builder(this)
                    .setTitle(R.string.alert_title_confirm_payment)
                    .setMessage(getString(R.string.alert_message_confirm_payment, price))
                    .setPositiveButton(R.string.yes_button) { _, _ ->
                        val paymentData = intent.getSerializableExtra("paymentData") as? Map<String, Any>
                        val token = paymentData?.get("token") as? String

                        if(!token?.let { it1 -> WalletManager.isPayable(it1) }!!){
                            Toast.makeText(this, "Este pago ya ha sido realizado.", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                        // Realizar el pago
                        WalletData.saldo -= price

                        // Elimina los descuentos aplicados
                        selectedDiscounts.forEach {
                            WalletData.discounts.removeAt(it)
                        }
                        recordPayment(paymentData, price) // Registrar el pago

                        updateIndicators(tvSaldo, tvPuntos) // Actualizar indicadores
                        WalletManager.processPayment(token, WalletData.userId) // Marcar el pago como realizado

                        Toast.makeText(
                            this,
                            R.string.payment_successful_message,
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navegar a la actividad de confirmación
                        val intent = Intent(this, PaymentConfirmationActivity::class.java)

                        intent.putExtra("price", price)
                        intent.putExtra("voucher", paymentData["voucher"] as? Boolean ?: false)
                        startActivity(intent)



                    }
                    .setNegativeButton(R.string.no_button, null)
                    .show()
            }
        }
    }

    private fun recordPayment(data: Map<String, Any>, price: Double) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val transaction = mapOf(
            "data" to data,
            "method" to "money",
            "price" to price,
            "date" to currentDate
        )
        WalletData.transactions.add(transaction)

        if(data["voucher"] == true){
            WalletData.vouchers.add(data)
        }
    }

}




