package com.example.npi_app

import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageButton

class DocenciaActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.docencia)

        // Obtén la referencia a la toolbar inferior desde el archivo XML
        val downToolBar: GridLayout = findViewById(R.id.down_toolbar)

        // Crea una instancia de ToolbarInferior para gestionar el comportamiento del toolbar
        DownToolBar(this, downToolBar)

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
