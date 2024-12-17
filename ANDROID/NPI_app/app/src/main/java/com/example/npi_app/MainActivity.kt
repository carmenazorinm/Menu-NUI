package com.example.npi_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.npi_app.ui.theme.NPI_appTheme
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Aquí puedes manipular el TextView
        val tituloMenu = findViewById<TextView>(R.id.titulo_menu)
        tituloMenu.text = "MENÚ PRINCIPAL"

        // Obtén la referencia a la toolbar inferior desde el archivo XML
        val downToolBar: LinearLayout = findViewById(R.id.down_toolbar)

        // Crea una instancia de ToolbarInferior para gestionar el comportamiento del toolbar
        DownToolBar(this, downToolBar)

        // Encontramos el botón por su ID
        val aulasButton: ImageButton = findViewById(R.id.btn_aulas)

        // Establecemos el listener para el clic del botón
        aulasButton.setOnClickListener {
            // Crear un Intent para iniciar la actividad LocalizacionActivity
            val intent = Intent(this@MainActivity, Localizacion::class.java)
            startActivity(intent)
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NPI_appTheme {
        Greeting("Android")
    }
}