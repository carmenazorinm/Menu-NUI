package com.example.npi_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class RedeemConfirmationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redeem_confirmed_layout)

        val type = intent.getStringExtra("redeem_type")
        val tvMessage: TextView = findViewById(R.id.tvRedeemMessage)

        val message = when (type) {
            "discount" -> getString(R.string.discount_redeemed)
            "product" -> getString(R.string.product_redeemed)
            else -> getString(R.string.redeem_completed)
        }

        tvMessage.text = message

        findViewById<Button>(R.id.btnExit).setOnClickListener {
            val intent = Intent(this, WalletEntryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
