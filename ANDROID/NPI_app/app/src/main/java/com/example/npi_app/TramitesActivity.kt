package com.example.npi_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton

class TramitesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tramites)

        findViewById<ImageButton>(R.id.btn_informatica).setOnClickListener {
            startActivity(Intent(this, InformaticaActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_teleco).setOnClickListener {
            startActivity(Intent(this, TelecomunicacionesActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_dgiim).setOnClickListener {
            startActivity(Intent(this, DGIIMActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_dgiiade).setOnClickListener {
            startActivity(Intent(this, DGIIADEActivity::class.java))
        }
    }
}
