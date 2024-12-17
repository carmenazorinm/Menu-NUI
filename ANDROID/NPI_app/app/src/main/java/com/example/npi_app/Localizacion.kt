package com.example.npi_app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.npi_app.ui.theme.NPI_appTheme

class Localizacion : ComponentActivity() {
    var txtPosicion: EditText?=null
    var sm: SensorManager?=null
    var sa: Sensor?=null
    var SEL: SensorEventListener?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.localizacion)
        txtPosicion=findViewById(R.id.txtPosicion)

        sm=getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sa=sm?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sa==null){
            Toast.makeText(this, "No hay sensor", Toast.LENGTH_LONG).show()
        } else {
            SEL = object : SensorEventListener{
                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

                }
                override fun onSensorChanged(event: SensorEvent?) {
                    var x=event!!.values[0]
                    txtPosicion?.setText(x.toString())
                }
            }
        }
    }
    fun iniciar() {
        sm?.registerListener(SEL,sa,SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onResume() {
        iniciar()
        super.onResume()
    }

    fun detener() {
        sm?.unregisterListener(SEL)
    }

    override fun onStop() {
        detener()
        super.onStop()
    }
}