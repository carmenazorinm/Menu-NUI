package com.example.npi_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton

class ProfesoradoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profesorado)

        findViewById<ImageButton>(R.id.btn_horario).setOnClickListener {
            startActivity(Intent(this, NFCActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_calendario).setOnClickListener {
            startActivity(Intent(this, CalendarioAcademicoActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_examenes).setOnClickListener {
            startActivity(Intent(this, CalendarioExamenesActivity::class.java))
        }
    }
}