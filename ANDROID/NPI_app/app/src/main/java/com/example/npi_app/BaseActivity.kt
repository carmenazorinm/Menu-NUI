package com.example.npi_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    // Variables para el manejo de deslizamientos
    private var initialX = floatArrayOf(0f, 0f) // Array para las posiciones iniciales de ambos dedos en el eje X
    private val displacementThreshold = 800f // Umbral de desplazamiento
    private var isActivityStarted = false // Evitar inicio múltiple de la actividad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Listener para los toques y deslizamientos
        val touchListener = object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // Guardar las posiciones iniciales de los dedos (en el eje X)
                            Log.d("dedos", "Toque detectado")
                            if (event.pointerCount >= 2) {
                                Log.d("dedos", "Más de 1 toque detectado")
                                initialX[0] = event.getX(0)
                                initialX[1] = event.getX(1)
                            }
                        }

                        MotionEvent.ACTION_MOVE -> {
                            Log.d("dedos", "Deslizamiento detectado")
                            if (event.pointerCount == 2) {
                                Log.d("dedos", "Doble deslizamiento")
                                val firstDisplacement = event.getX(0) - initialX[0]
                                val secondDisplacement = event.getX(1) - initialX[1]

                                // Verificar si ambos dedos se desplazaron más allá del umbral en el eje X (horizontal)
                                if (!isActivityStarted && firstDisplacement > displacementThreshold && secondDisplacement > displacementThreshold) {
                                    isActivityStarted = true
                                    Log.d("dedos", "umbral superado")
                                    // Volver a la pantalla principal (MainActivity)
                                    val intent = Intent(this@BaseActivity, MainActivity::class.java)
                                    Log.d("dedos", "Pantalla principal")
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    startActivity(intent)
                                    // Reseteamos las posiciones iniciales después de detectar el movimiento
                                    initialX[0] = 0f
                                    initialX[1] = 0f
                                    return true
                                }
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            // Resetear las posiciones iniciales cuando se levanten los dedos
                            initialX[0] = 0f
                            initialX[1] = 0f
                            isActivityStarted = false // Restablecer el estado después de soltar los dedos
                        }
                    }
                }
                return true
            }
        }

        // Aplicar el listener al layout de la actividad
        val layout = findViewById<View>(android.R.id.content)
        layout.setOnTouchListener(touchListener)
    }

}
