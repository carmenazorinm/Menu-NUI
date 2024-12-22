package com.example.npi_app

import android.os.Bundle
import android.widget.ImageButton
import java.util.Locale

class AjustesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ajustes)

        // Referencia a los botones de idioma
        val btnEspanol = findViewById<ImageButton>(R.id.btn_espanol)
        val btnIngles = findViewById<ImageButton>(R.id.btn_ingles)
        val btnAleman = findViewById<ImageButton>(R.id.btn_aleman)

        // Configuración de los listeners para cambiar el idioma
        btnEspanol.setOnClickListener {
            setLocale("es") // Cambia a español
        }

        btnIngles.setOnClickListener {
            setLocale("en") // Cambia a inglés
        }

        btnAleman.setOnClickListener {
            setLocale("de") // Cambia a alemán
        }
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Reinicia la actividad para aplicar los cambios de idioma
        recreate()
    }
}
