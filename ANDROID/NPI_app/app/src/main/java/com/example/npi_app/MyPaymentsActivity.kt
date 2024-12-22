package com.example.npi_app

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

class MyPaymentsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_payments_layout)

        val rvTransactions: RecyclerView = findViewById(R.id.rvTransactions)
        val btnBack: Button = findViewById(R.id.btnBack)

        // Configurar RecyclerView
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.adapter = TransactionsAdapter(WalletData.transactions, this)

        // Volver a la actividad principal
        btnBack.setOnClickListener { finish() }
    }
}
