package com.example.npi_app

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AlertDialog
import java.util.Locale

class DownToolBar(private val context: Context, private val toolbar: GridLayout) : BaseActivity() {

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
            showLanguageDialog()
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

    private fun showLanguageDialog() {
        val languages = arrayOf("Español", "English")
        val localeCodes = arrayOf("es", "en")
        val localeFlags = arrayOf(R.drawable.ic_flag_spanish, R.drawable.ic_flag_english)
        AlertDialog.Builder(this, R.style.MyDialogTheme)
            .setTitle(R.string.language)
            .setItems(languages) { _, which ->
                setLocale(localeCodes[which])
                findViewById<ImageButton>(R.id.btn_language).setImageResource(localeFlags[which])
            }
            .show()


    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        LocaleManager.setLocale(this, locale.language)

        // Reiniciar actividad para aplicar cambios
        //recreate()
    }
}