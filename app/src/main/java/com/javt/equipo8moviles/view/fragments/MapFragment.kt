    package com.javt.equipo8moviles.view.fragments

    import android.content.Intent
    import android.graphics.Color
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.activityViewModels
    import com.javt.equipo8moviles.databinding.FragmentMapBinding
    import com.javt.equipo8moviles.model.Imagen
    import com.javt.equipo8moviles.view.PuntuacionActivity
    import com.javt.equipo8moviles.viewmodel.JuegoViewModel
    import org.osmdroid.config.Configuration
    import org.osmdroid.events.MapEventsReceiver
    import org.osmdroid.tileprovider.tilesource.TileSourceFactory
    import org.osmdroid.util.GeoPoint
    import org.osmdroid.views.MapView
    import org.osmdroid.views.overlay.MapEventsOverlay
    import org.osmdroid.views.overlay.Marker
    import org.osmdroid.views.overlay.Polygon
    import kotlin.math.abs
    import kotlin.math.atan2
    import kotlin.math.cos
    import kotlin.math.sin
    import kotlin.math.sqrt


    class MapFragment : Fragment(), MapEventsReceiver {

        private var _binding: FragmentMapBinding? = null
        private val binding get() = _binding!!
        private lateinit var mapView: MapView
        private var marker: Marker? = null
        private var circleOverlay: Polygon? = null
        private val viewModel: JuegoViewModel by activityViewModels()
        private var imagenActual: Imagen? = null

        companion object {
            private const val ARG_NOMBRE_IMAGEN = "nombre_imagen"

            fun newInstance(nombreImagen: String): MapFragment {
                return MapFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOMBRE_IMAGEN, nombreImagen)
                    }
                }
            }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = FragmentMapBinding.inflate(inflater, container, false)
            val view = binding.root
            mapView = binding.mapView
            setupMap()
            return view
        }

        private fun setupMap() {
            Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(0))
            mapView.setTileSource(TileSourceFactory.OpenTopo) // estilo de mapa, tarda un poco mas en cargar que el mapa por defecto
            mapView.setMultiTouchControls(true)

            // Configurar el centro del mapa en España (latitud y longitud aproximadas)
            val mapController = mapView.controller
            mapController.setZoom(6.8)
            mapController.setCenter(GeoPoint(40.4168, -3.7038)) // Madrid, España

            mapView.overlays.add(MapEventsOverlay(this))

            val nombreImagen = arguments?.getString(ARG_NOMBRE_IMAGEN)
            imagenActual = viewModel.obtenerImagenesSegunDificultad(0).find { it.nombre == nombreImagen }

            // Verificar si la imagen ya ha sido acertada
            if (imagenActual != null && viewModel.imagenYaAcertada(imagenActual!!)) {
                Toast.makeText(requireContext(), "Ya acertaste esta imagen", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .remove(this@MapFragment)
                    .commit()
            }
        }

        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
            if (p == null || imagenActual == null) return false

            // Verificar si la imagen ya ha sido acertada
            if (viewModel.imagenYaAcertada(imagenActual!!)) {
                Toast.makeText(requireContext(), "Ya acertaste esta imagen", Toast.LENGTH_SHORT).show()
                return false
            }

            marker?.let { mapView.overlays.remove(it) }
            circleOverlay?.let { mapView.overlays.remove(it) }

            marker = Marker(mapView).apply {
                position = p
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            mapView.overlays.add(marker)

            circleOverlay = createCircle(p, 100000.0)
            mapView.overlays.add(circleOverlay)

            val distancia = calcularDistancia(p, GeoPoint(imagenActual!!.latitud, imagenActual!!.longitud))
            val acierto = distancia <= 100000.0

            viewModel.procesarIntento(acierto,  imagenActual!!)

            requireActivity().runOnUiThread {
                val mensaje = if (acierto) {
                    // Mueve la cámara al punto de la imagen
                    val puntoAcierto = GeoPoint(imagenActual!!.latitud, imagenActual!!.longitud)
                    mapView.controller.setCenter(puntoAcierto)  // Centra la cámara en el punto
                    mapView.controller.setZoom(8.0) // Ajusta el nivel de zoom

                    // Registrar la imagen como acertada
                    viewModel.agregarImagenAcertada(imagenActual!!)

                    "¡Correcto! Era ${imagenActual!!.lugar}"
                } else {
                    val pista = obtenerPista(p, GeoPoint(imagenActual!!.latitud, imagenActual!!.longitud))
                    "Fallaste. Prueba más al $pista"
                }
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()

                // Si se acaban los intentos, cerrar el fragmento
                if (viewModel.juegoTerminado() || viewModel.imagenesAcertadas.value?.size == viewModel.obtenerImagenesSegunDificultad(0).size) {
                    Toast.makeText(requireContext(), "Juego terminado, sin intentos restantes o todas las imágenes acertadas.", Toast.LENGTH_LONG).show()

                    // Navegar a la pantalla de puntuación
                    val intent = Intent(requireContext(), PuntuacionActivity::class.java)
                    intent.putExtra("imagenesAcertadas", ArrayList(viewModel.imagenesAcertadas.value ?: emptyList()))
                    startActivity(intent)

                    // Cierra la actividad de PantallaImagenes y vuelve a la principal
                    requireActivity().finish()
                }
            }

            mapView.invalidate()

            return true
        }

        override fun longPressHelper(p: GeoPoint?): Boolean {
            return false
        }

        private fun createCircle(center: GeoPoint, radius: Double): Polygon {
            return Polygon().apply {
                points = Polygon.pointsAsCircle(center, radius)
                fillColor = 0x40FF0000
                strokeColor = Color.RED
                strokeWidth = 3f
            }
        }

        private fun calcularDistancia(p1: GeoPoint, p2: GeoPoint): Double {
            // Formula de Haversine para calcular la distancia entre dos puntos
            val r = 6371000
            val dLat = Math.toRadians(p2.latitude - p1.latitude)
            val dLon = Math.toRadians(p2.longitude - p1.longitude)

            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(p1.latitude)) * cos(Math.toRadians(p2.latitude)) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return r * c
        }

        private fun obtenerPista(userPoint: GeoPoint, correctPoint: GeoPoint): String {
            // Calculamos la diferencia entre latitudes y longitudes
            val latitudDiferencia = abs(userPoint.latitude - correctPoint.latitude)
            val longitudDiferencia = abs(userPoint.longitude - correctPoint.longitude)

            // Comparamos las diferencias
            return if (latitudDiferencia > longitudDiferencia) {
                // Si la diferencia de latitudes es mayor
                if (userPoint.latitude < correctPoint.latitude) {
                    "norte"
                } else {
                    "sur"
                }
            } else {
                // Si la diferencia de longitudes es mayor
                if (userPoint.longitude < correctPoint.longitude) {
                    "este"
                } else {
                    "oeste"
                }
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
