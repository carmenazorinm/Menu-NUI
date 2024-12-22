package com.example.npi_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
import org.json.JSONObject

class MyVouchersActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_vouchers_layout)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewVouchers)
        val btnBack: Button = findViewById(R.id.btnBack)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = VouchersAdapter(WalletData.vouchers) { voucher ->
            val intent = Intent(this, VoucherDetailsActivity::class.java)
            intent.putExtra("voucher", JSONObject(voucher).toString())
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
