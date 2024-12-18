package com.example.npi_app

import android.content.Context
import android.content.Intent
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar

class DownToolBar(private val context: Context, private val toolbar: LinearLayout) {

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