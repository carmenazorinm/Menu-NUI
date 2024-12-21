package com.example.npi_app

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions

private val LOCATION_PERMISSION_REQUEST = 1

class MapActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapa)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtener el fragmento del mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Aplica el estilo personalizado
        setCustomMapStyle()

        map.clear()

        checkLocationPermission()
        showCurrentLocation()

        // Configura el evento de clic en el marcador
        map.setOnMarkerClickListener { marker ->
            marker.showInfoWindow() // Mostrar el título cuando se haga clic
            showGetDirectionsButton(marker)
            true // Devuelve true para indicar que manejaste el evento
        }

        // Coordenadas iniciales (ejemplo: Biblioteca Central)
        val biblioteca = LatLng(37.196912, -3.624452)
        map.addMarker(MarkerOptions().position(biblioteca).title("Biblioteca"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(biblioteca, 15f))

        val comedor = LatLng(37.196972, -3.624482)
        map.addMarker(MarkerOptions().position(comedor).title("Comedor"))

        val cafeteria = LatLng(37.196962, -3.624617)
        map.addMarker(MarkerOptions().position(cafeteria).title("Cafetería"))

        val aulas = LatLng(37.197324, -3.624294)
        map.addMarker(MarkerOptions().position(aulas).title("Aulas"))

        val deiit = LatLng(37.197295, -3.624024)
        map.addMarker(MarkerOptions().position(deiit).title("DEIIT"))

        val citic = LatLng(37.197730, -3.623951)
        map.addMarker(MarkerOptions().position(citic).title("CITIC"))

        val aulas_prefab = LatLng(37.196959, -3.623969)
        map.addMarker(MarkerOptions().position(aulas_prefab).title("Aulario Exterior"))
    }

    private fun showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    //map.addMarker(MarkerOptions().position(currentLatLng).title("Mi ubicación"))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            map.isMyLocationEnabled = true
        }
    }

    private fun setCustomMapStyle() {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )
            if (!success) {
                Log.d("Mapa", "Error al aplicar el estilo del mapa")
            }
        } catch (e: Resources.NotFoundException) {
            Log.d("Mapa", "Archivo de estilo no encontrado. Error: ", e)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled = true
            }
        }
    }

    // Función para calcular la distancia en metros entre dos puntos
    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000 // Radio de la Tierra en metros
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c // Resultado en metros
    }

    private fun showGetDirectionsButton(marker: Marker) {
        val button = findViewById<ImageButton>(R.id.btn_ruta)
        button.visibility = View.VISIBLE
        button.setOnClickListener {
            // Al hacer clic, llamamos a la función para calcular la ruta
            calculateAndDisplayRoute(marker)
        }
    }

    @SuppressLint("MissingPermission")
    private fun calculateAndDisplayRoute(destinationMarker: Marker) {
        // Obtener la ubicación actual del usuario
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val originLatLng = LatLng(location.latitude, location.longitude)
                    val destinationLatLng = destinationMarker.position

                    // Calcular la distancia utilizando la función Haversine
                    val distance = haversine(originLatLng.latitude, originLatLng.longitude,
                        destinationLatLng.latitude, destinationLatLng.longitude)

                    // Mostrar la distancia en el mapa o en un texto
                    Toast.makeText(this, "Distancia: ${distance/1000} km", Toast.LENGTH_SHORT).show()

                    // Dibuja la ruta entre los puntos
                    drawRoute(originLatLng, destinationLatLng)
                }
            }
    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {
        // Limpiar las rutas anteriores si es necesario
        map.clear()
        map.addMarker(MarkerOptions().position(destination))

        // Dibujar la línea entre los dos puntos
        val polylineOptions = PolylineOptions()
            .add(origin)  // Origen
            .add(destination)  // Destino
            .color(Color.RED)  // Color de la línea
            .width(5f)  // Ancho de la línea

        map.addPolyline(polylineOptions)  // Añadir la línea al mapa
    }


}
