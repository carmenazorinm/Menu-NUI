package com.example.npi_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton

class DocenciaActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.docencia)

        findViewById<ImageButton>(R.id.btn_horario).setOnClickListener {
            startActivity(Intent(this, NFCActivity::class.java))
        }
    }
}
