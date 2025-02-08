package com.javt.equipo8moviles.view.fragments

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
import com.javt.equipo8moviles.viewmodel.JuegoViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import kotlin.math.cos
import kotlin.math.sin


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
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(5.0)
        mapView.overlays.add(MapEventsOverlay(this))

        val nombreImagen = arguments?.getString(ARG_NOMBRE_IMAGEN)
        imagenActual = viewModel.obtenerImagenesSegunDificultad(0).find { it.nombre == nombreImagen }

        // Verificar si la imagen ya ha sido acertada
        if (imagenActual != null && viewModel.imagenYaAcertada(imagenActual!!)) {
            Toast.makeText(requireContext(), "Ya acertaste esta imagen", Toast.LENGTH_SHORT).show()
            // Si ya se ha acertado, puedes cerrar el fragmento o hacer lo que necesites
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

        viewModel.procesarIntento(acierto, System.currentTimeMillis(), imagenActual!!)

        requireActivity().runOnUiThread {
            val mensaje = if (acierto) {
                // Mueve la cámara al punto de la imagen
                val puntoAcierto = GeoPoint(imagenActual!!.latitud, imagenActual!!.longitud)
                mapView.controller.setCenter(puntoAcierto)  // Centra la cámara en el punto
                mapView.controller.setZoom(7.0) // Ajusta el nivel de zoom

                // Registrar la imagen como acertada
                viewModel.agregarImagenAcertada(imagenActual!!)

                "¡Correcto! Era ${imagenActual!!.lugar}"
            } else {
                val pista = obtenerPista(p, GeoPoint(imagenActual!!.latitud, imagenActual!!.longitud))
                "Fallaste. Prueba más al $pista"
            }
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()

            // Si se acaban los intentos, cerrar el fragmento
            if (viewModel.juegoTerminado()) {
                Toast.makeText(requireContext(), "Juego terminado, sin intentos restantes.", Toast.LENGTH_LONG).show()

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

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(p1.latitude)) * cos(Math.toRadians(p2.latitude)) *
                Math.sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return r * c
    }

    private fun obtenerPista(userPoint: GeoPoint, correctPoint: GeoPoint): String {
        // Calculamos la diferencia entre latitudes y longitudes
        val latitudDiferencia = Math.abs(userPoint.latitude - correctPoint.latitude)
        val longitudDiferencia = Math.abs(userPoint.longitude - correctPoint.longitude)

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
