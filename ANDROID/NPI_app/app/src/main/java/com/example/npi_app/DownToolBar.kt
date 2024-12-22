package com.example.npi_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*

class DownToolBar(private val context: Context, private val toolbar: GridLayout) {

    companion object {
        var login: Boolean = false
    }

    init {

        // Inicializamos los elementos de la toolbar inferior
        val loginButton: ImageButton = toolbar.findViewById(R.id.btn_usuario)
        val btnAjustes: ImageButton = toolbar.findViewById(R.id.btn_ajustes)
        val ic_usuario: ImageView = toolbar.findViewById(R.id.btn_usuario)

        // Actualizamos la imagen del botón según el estado de login
        updateLoginIcon(ic_usuario)

        // Listener para el botón de login
        loginButton.setOnClickListener {
            if (!login) {
                // Navegar a la pantalla de login
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }
        }

        // Establecemos el listener para el clic del botón de ajustes
        btnAjustes.setOnClickListener {
            // Navega a la pantalla de ajustes
            val intent = Intent(context, AjustesActivity::class.java)
            context.startActivity(intent)
        }
    }

    // Función para actualizar el ícono del botón según el estado de login
    private fun updateLoginIcon(ic_usuario: ImageView) {
        if (login) {
            ic_usuario.setImageResource(R.drawable.ic_login_correcto) // Si pongo la foto de marcelino se ve sólo blanco
        } else {
            ic_usuario.setImageResource(R.drawable.ic_usuario) // Ícono predeterminado
        }
    }
}