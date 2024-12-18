package com.example.npi_app

import android.content.Context
import android.content.Intent
import android.widget.GridLayout
import android.widget.ImageButton

class DownToolBar(private val context: Context, private val toolbar: GridLayout) {

    init {
        // Inicializa los elementos de la toolbar inferior
        val btnAjustes: ImageButton = toolbar.findViewById(R.id.btn_ajustes)

        // Configura el onClickListener para el botón de ajustes
        btnAjustes.setOnClickListener {
            // Navega a la pantalla de ajustes
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }
}