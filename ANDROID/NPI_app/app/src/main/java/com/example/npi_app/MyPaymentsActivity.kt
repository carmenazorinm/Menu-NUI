package com.example.npi_app

import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
