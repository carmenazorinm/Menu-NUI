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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlin.math.max
import kotlin.math.min

private val LOCATION_PERMISSION_REQUEST = 1

// Triángulo de la ETSIIT
val vertex1 = LatLng(37.196411, -3.624360) // Primer vértice
val vertex2 = LatLng(37.197556, -3.625165) // Segundo vértice
val vertex3 = LatLng(37.197436, -3.623304) // Tercer vértice

data class Nodo(
    val nombre: String,
    val posicion: LatLng,
    val planta: String = "",
    val conexiones: MutableList<Nodo> = mutableListOf()
) {
    // Sobrescribir el método hashCode para evitar la recursión infinita
    override fun hashCode(): Int {
        // Solo usar el nombre y la posición para calcular el hash, no las conexiones
        return 31 * nombre.hashCode() + posicion.hashCode()
    }
}



class MapActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // CREACIÓN DE NODOS

    // NODOS A LOS QUE IR
    val biblioteca = Nodo("Biblioteca", LatLng(37.196963, -3.624449), "1")
    val comedor = Nodo("Comedor", LatLng(37.196972, -3.624482), "-1")
    val cafeteria = Nodo("Cafetería", LatLng(37.196962, -3.624617), "-1")
    val aulas = Nodo("Aulas", LatLng(37.197324, -3.624294), "0 a 3")
    val deiit = Nodo("DEIIT", LatLng(37.197295, -3.624024), "4")
    val aulasPrefab = Nodo("Aulario B", LatLng(37.196959, -3.623969), "0")
    val secretaria = Nodo("Secretaría", LatLng(37.197159, -3.624551), "0")
    val porteria = Nodo("Portería", LatLng(37.196944, -3.624510), "0")
    val despacho = Nodo("Despachos", LatLng(37.197017, -3.624507), "2 a 4")
    // PUNTOS DE PASO
    val entrada = Nodo("Entrada", LatLng(37.196750, -3.624377))
    val mesasToldo = Nodo("Mesas con Toldo", LatLng(37.197018, -3.624191))
    val rampa = Nodo("Rampa", LatLng(37.196913, -3.624288))
    val pasilloExterior = Nodo("Pasillo Exterior", LatLng(37.197102, -3.624270))
    val puertaFuera = Nodo("Puerta Exterior", LatLng(37.196999, -3.624333))
    val escalerasBiblioteca = Nodo("Escaleras Biblioteca", LatLng(37.196931, -3.624449))
    val escalerasComedor = Nodo("Escaleras Comedor", LatLng(37.196961, -3.624364))

    val nodos = listOf(
        biblioteca, comedor, cafeteria, aulas, deiit, aulasPrefab,
        secretaria, porteria, despacho, entrada, mesasToldo, rampa,
        pasilloExterior, puertaFuera, escalerasBiblioteca, escalerasComedor
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapa)

        val mensaje_planta = findViewById<TextView>(R.id.mensaje_planta)
        mensaje_planta.visibility = View.GONE

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

        // CONEXIONES ENTRE NODOS

        biblioteca.conexiones.add(escalerasBiblioteca)
        comedor.conexiones.add(cafeteria)
        comedor.conexiones.add(escalerasComedor)
        cafeteria.conexiones.add(comedor)
        cafeteria.conexiones.add(escalerasComedor)
        aulas.conexiones.add(pasilloExterior)
        aulas.conexiones.add(deiit)
        deiit.conexiones.add(aulas)
        aulasPrefab.conexiones.add(mesasToldo)
        aulasPrefab.conexiones.add(entrada)
        secretaria.conexiones.add(porteria)
        secretaria.conexiones.add(escalerasComedor)
        secretaria.conexiones.add(escalerasBiblioteca)
        secretaria.conexiones.add(puertaFuera)
        secretaria.conexiones.add(entrada)
        porteria.conexiones.add(secretaria)
        porteria.conexiones.add(entrada)
        porteria.conexiones.add(escalerasBiblioteca)
        porteria.conexiones.add(escalerasComedor)
        porteria.conexiones.add(puertaFuera)
        despacho.conexiones.add(escalerasBiblioteca)
        entrada.conexiones.add(rampa)
        entrada.conexiones.add(secretaria)
        entrada.conexiones.add(porteria)
        entrada.conexiones.add(aulasPrefab)
        entrada.conexiones.add(escalerasBiblioteca)
        mesasToldo.conexiones.add(puertaFuera)
        mesasToldo.conexiones.add(aulasPrefab)
        mesasToldo.conexiones.add(pasilloExterior)
        mesasToldo.conexiones.add(rampa)
        rampa.conexiones.add(pasilloExterior)
        rampa.conexiones.add(puertaFuera)
        rampa.conexiones.add(entrada)
        rampa.conexiones.add(mesasToldo)
        pasilloExterior.conexiones.add(rampa)
        pasilloExterior.conexiones.add(puertaFuera)
        pasilloExterior.conexiones.add(mesasToldo)
        pasilloExterior.conexiones.add(aulas)
        puertaFuera.conexiones.add(pasilloExterior)
        puertaFuera.conexiones.add(mesasToldo)
        puertaFuera.conexiones.add(rampa)
        puertaFuera.conexiones.add(secretaria)
        puertaFuera.conexiones.add(porteria)
        puertaFuera.conexiones.add(escalerasComedor)
        escalerasBiblioteca.conexiones.add(biblioteca)
        escalerasBiblioteca.conexiones.add(despacho)
        escalerasBiblioteca.conexiones.add(porteria)
        escalerasBiblioteca.conexiones.add(entrada)
        escalerasBiblioteca.conexiones.add(secretaria)
        escalerasComedor.conexiones.add(puertaFuera)
        escalerasComedor.conexiones.add(comedor)
        escalerasComedor.conexiones.add(cafeteria)
        escalerasComedor.conexiones.add(secretaria)
        escalerasComedor.conexiones.add(porteria)

        // MARCADORES A VISUALIZAR EN EL MAPA

        map.addMarker(MarkerOptions().position(biblioteca.posicion).title("Biblioteca"))
        map.addMarker(MarkerOptions().position(comedor.posicion).title("Comedor"))
        map.addMarker(MarkerOptions().position(cafeteria.posicion).title("Cafetería"))
        map.addMarker(MarkerOptions().position(aulas.posicion).title("Aulas"))
        map.addMarker(MarkerOptions().position(deiit.posicion).title("DEIIT"))
        map.addMarker(MarkerOptions().position(aulasPrefab.posicion).title("Aulario B"))
        map.addMarker(MarkerOptions().position(secretaria.posicion).title("Secretaría"))
        map.addMarker(MarkerOptions().position(porteria.posicion).title("Portería"))
        map.addMarker(MarkerOptions().position(despacho.posicion).title("Despachos"))

        // Crear límites que incluyan los tres vértices
        val bounds = LatLngBounds.Builder()
            .include(vertex1)
            .include(vertex2)
            .include(vertex3)
            .build()

        // Ajustar el enfoque del mapa para que los límites sean visibles
        val padding = 100 // Espacio adicional en píxeles alrededor de los límites
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))

        // Configura el evento de clic en el marcador
        map.setOnMarkerClickListener { marker ->
            marker.showInfoWindow() // Mostrar el título cuando se haga clic

            // Obtener el nodo asociado con el marcador (asegúrate de tener esta lógica de asociación)
            val nodoDestino = nodos.find { it.posicion == marker.position }
            if (nodoDestino != null) {
                // Obtener la ubicación actual del usuario antes de mostrar el botón de ruta
                obtenerUbicacionActual { ubicacionActual ->
                    if (estaDentroDeLaFacultad(ubicacionActual)) {
                        mostrarBotonRuta(ubicacionActual, nodoDestino)
                    }
                }
            }
            true
        }
    }

    // Función para obtener la ubicación actual del usuario
    private fun obtenerUbicacionActual(callback: (LatLng) -> Unit) {
        // Verifica los permisos de ubicación antes de intentar obtener la ubicación actual
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    // Llama al callback con la ubicación actual
                    val ubicacionActual = LatLng(it.latitude, it.longitude)
                    callback(ubicacionActual)
                } ?: run {
                    // En caso de no tener ubicación disponible
                    Log.e("Ubicación", "No se pudo obtener la ubicación actual.")
                }
            }
        } else {
            // Solicitar permiso si no está concedido
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        }
    }

    private fun mostrarBotonRuta(ubicacionActual: LatLng, nodoDestino: Nodo) {
        val botonRuta = findViewById<ImageButton>(R.id.btn_ruta)
        botonRuta.visibility = View.VISIBLE
        botonRuta.setOnClickListener {
            calcularYMostrarRuta(ubicacionActual, nodoDestino)
        }
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
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    if (!estaDentroDeLaFacultad(currentLatLng)) {
                        Toast.makeText(this, "Vaya! Parece que no estás en la ETSIIT", Toast.LENGTH_SHORT).show()
                    }
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
<<<<<<< HEAD
                Log.e("MapActivity", "Error al aplicar el estilo del mapa")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapActivity", "Archivo de estilo no encontrado. Error: ", e)
        }
    }


=======
                Log.d("Map", "Error al aplicar el estilo del mapa")
            }
        } catch (e: Resources.NotFoundException) {
            Log.d("Map", "Archivo de estilo no encontrado. Error: ", e)
        }
    }

>>>>>>> origin/main
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

    fun encontrarRuta(inicio: Nodo, destino: Nodo): List<Nodo>? {
        //Log.d("Map", "Iniciando búsqueda de ruta desde: $inicio a $destino")  // Verifica si entra aquí
        val visitados = mutableSetOf<Nodo>()
        val cola = ArrayDeque<List<Nodo>>()
        cola.add(listOf(inicio))

        while (cola.isNotEmpty()) {
            val ruta = cola.removeFirst()
            val nodoActual = ruta.last()

            //Log.d("Map","Ruta actual: $ruta")

            if (nodoActual == destino) return ruta

            if (nodoActual !in visitados) {
                visitados.add(nodoActual)
                for (vecino in nodoActual.conexiones) {
                    // Evitar volver al nodo anterior
                    if (vecino !in visitados && (ruta.size == 1 || vecino != ruta[ruta.size - 2])) {
                        cola.add(ruta + vecino)
                    }
                }
            }
        }
        return null // No hay ruta
    }


    private fun drawRoute(destination: LatLng, ruta: List<Nodo>) {

        // Añadir el log con los nombres de los nodos de la ruta
        Log.d("Map", "Ruta a dibujar: ${ruta.map { it.nombre }}")

        Log.d("Map", "Dibujando ruta")

        // Limpiar las rutas anteriores si es necesario
        map.clear()
        map.addMarker(MarkerOptions().position(destination))
        Log.d("Map", "He limpiado el mapa")

        // Añadir el log con los nombres de los nodos de la ruta
        Log.d("Map", "Ruta a dibujar: ${ruta.map { it.nombre }}")

        val polylineOptions = PolylineOptions().color(Color.RED).width(5f)
        ruta.forEach { polylineOptions.add(it.posicion) }
        Log.d("Map", "Ahora dibujo la ruta")
        map.addPolyline(polylineOptions)
    }

    fun estaDentroDeLaFacultad(latLng: LatLng): Boolean {
        // Coordenadas de los vértices del triángulo

        val areaEtsiit = listOf(vertex1, vertex2, vertex3)

        var intersections = 0
        for (i in areaEtsiit.indices) {
            val j = (i + 1) % areaEtsiit.size
            val v1 = areaEtsiit[i]
            val v2 = areaEtsiit[j]

            // Verificación de cruce del borde
            if (latLng.latitude > min(v1.latitude, v2.latitude) &&
                latLng.latitude <= max(v1.latitude, v2.latitude) &&
                latLng.longitude <= max(v1.longitude, v2.longitude) &&
                v1.latitude != v2.latitude) {

                // Cálculo de la intersección
                val xIntersection = (latLng.latitude - v1.latitude) * (v2.longitude - v1.longitude) /
                        (v2.latitude - v1.latitude) + v1.longitude

                // Verificar si la intersección está a la derecha del punto
                if (xIntersection > latLng.longitude) {
                    intersections++
                }
            }
        }

        // Si el número de cruces es impar, el punto está dentro
        return intersections % 2 != 0
    }

    private fun obtenerNodoMasCercano(ubicacionActual: LatLng, nodos: List<Nodo>): Nodo? {
        Log.d("Map", "Se va a buscar el nodo más cercano")
        return nodos.minByOrNull { nodo ->
            calcularDistancia(ubicacionActual, nodo.posicion)
        }
    }

    private fun calcularDistancia(p1: LatLng, p2: LatLng): Double {
        Log.d("Map", "Calculando distancia!!")
        val latDiff = p1.latitude - p2.latitude
        val lngDiff = p1.longitude - p2.longitude

        // Aproximación de la distancia en metros
        val latDist = latDiff * 111139 // 1 grado de latitud = 111,139 metros
        val lngDist = lngDiff * 111139 * Math.cos(Math.toRadians(p1.latitude)) // 1 grado de longitud ≈ 111,139 * cos(lat) metros

        // Teorema de Pitágoras
        val distancia = Math.sqrt(latDist * latDist + lngDist * lngDist)

        Log.d("Map", "Distancia calculada: $distancia entre $p1 y $p2")
        return distancia
    }

    private fun calcularYMostrarRuta(ubicacionActual: LatLng, nodoDestino: Nodo) {

        Log.d("Map", "Calculando y mostrando ruta")
        // Encuentra el nodo más cercano a la ubicación actual
        val nodoCercano = obtenerNodoMasCercano(ubicacionActual, nodos)
        Log.d("Map", "Nodo más cercano: ${nodoCercano?.nombre} en ${nodoCercano?.posicion}")
        if (nodoCercano == null) {
            Log.d("Map", "No se encontró un nodo cercano")
            Toast.makeText(this, "No se encontró un nodo cercano", Toast.LENGTH_SHORT).show()
            return
        }

        // Asegúrate de que el flujo de ejecución sigue aquí
        Log.d("Map", "Continuando con la búsqueda de ruta")
        // Encuentra la ruta desde el nodo más cercano hasta el nodo destino
        Log.d("Map", "Iniciando búsqueda de ruta desde: ${nodoCercano?.nombre ?: "Desconocido"} a ${nodoDestino.nombre}")
        val rutaDesdeNodoCercano = encontrarRuta(nodoCercano, nodoDestino)

        if (rutaDesdeNodoCercano != null) {
            // Construye la ruta completa: primero desde la ubicación actual al nodo más cercano
            Log.d("Map", "Calculando ruta completa")
            val rutaCompleta = listOf(Nodo("Ubicación Actual", ubicacionActual)) + rutaDesdeNodoCercano

            val botonRuta = findViewById<ImageButton>(R.id.btn_ruta)
            botonRuta.visibility = View.GONE
            val mensaje_planta = findViewById<TextView>(R.id.mensaje_planta)
            mensaje_planta.text = "El destino se encuentra en la planta ${nodoDestino.planta}"
            mensaje_planta.visibility = View.VISIBLE

            drawRoute(nodoDestino.posicion, rutaCompleta)
        } else {
            Toast.makeText(this, "No se pudo calcular la ruta", Toast.LENGTH_SHORT).show()
        }
    }

}
