    package com.javt.equipo8moviles.view.fragments

    import android.content.Context
    import android.content.Intent
    import android.content.SharedPreferences
    import android.media.MediaPlayer
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.activityViewModels
    import com.javt.equipo8moviles.R
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


    class MapFragment : Fragment(), MapEventsReceiver {

        private var _binding: FragmentMapBinding? = null
        private val binding get() = _binding!!
        private lateinit var mapView: MapView
        private var marker: Marker? = null
        private var circleOverlay: Polygon? = null
        private val viewModel: JuegoViewModel by activityViewModels()
        private var imagenActual: Imagen? = null
        private lateinit var mediaPlayer : MediaPlayer
        private lateinit var prefs: SharedPreferences

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
            prefs=requireActivity().getSharedPreferences("JuegoPrefs", Context.MODE_PRIVATE)
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

            // Obtener el nombre imagen actual a partir de los argumentos
            val nombreImagen = arguments?.getString(ARG_NOMBRE_IMAGEN)
            // Obtener la imagen actual a partir de la lista de imágenes del ViewModel
            imagenActual = viewModel.obtenerImagenesSegunDificultad(viewModel.getDificultad()).find { it.nombre == nombreImagen }

            // Verificar si la imagen ya ha sido acertada
            if (imagenActual != null && viewModel.imagenYaAcertada(imagenActual!!)) {
                Toast.makeText(requireContext(), getString(R.string.ya_acertaste_esta_imagen), Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .remove(this@MapFragment)
                    .commit()
            }
        }

        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
            if (p == null || imagenActual == null) return false

            // Verificar si la imagen ya ha sido acertada
            if (viewModel.imagenYaAcertada(imagenActual!!)) {
                Toast.makeText(requireContext(),
                    getString(R.string.ya_acertaste_esta_imagen), Toast.LENGTH_SHORT).show()
                return false
            }

            marker?.let { mapView.overlays.remove(it) }
            circleOverlay?.let { mapView.overlays.remove(it) }

//             -- Marcador de la mano por si hace falta
//            marker = Marker(mapView).apply {
//                position = p
//                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//            }
//            mapView.overlays.add(marker)

            // Circulo de la imagen
            circleOverlay = viewModel.createCircle(p)
            mapView.overlays.add(circleOverlay)

            // Calcular la distancia entre el marcador y la imagen
            val distancia = viewModel.calcularDistancia(p, GeoPoint(imagenActual!!.latitud, imagenActual!!.longitud))
            val acierto=viewModel.isAcierto(distancia)

            // Registrar el intento en el ViewModel y actualizar la UI
            viewModel.procesarIntento(acierto,  imagenActual!!)

            requireActivity().runOnUiThread {
                val mensaje = if (acierto) {
                    // Mueve la cámara al punto de la imagen
                    val puntoAcierto = GeoPoint(imagenActual!!.latitud, imagenActual!!.longitud)
                    mapView.controller.setCenter(puntoAcierto)  // Centra la cámara en el punto (madrid)
                    mapView.controller.setZoom(8.0) // Ajusta el nivel de zoom

                    // Registrar la imagen como acertada
                    viewModel.agregarImagenAcertada(imagenActual!!)

                    getString(R.string.correct) +" "+ imagenActual!!.lugar
                } else {
                    val pista = obtenerPista(p, GeoPoint(imagenActual!!.latitud, imagenActual!!.longitud))
                    getString(R.string.wrong) +" "+ getString(R.string.try_again)+" "+ pista
                }
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()

                // Si se acaban los intentos, cerrar el fragmento
                if (viewModel.juegoTerminado() || viewModel.imagenesAcertadas.value?.size == viewModel.obtenerImagenesSegunDificultad(viewModel.getDificultad()).size) {
                    Toast.makeText(requireContext(),
                        getString(R.string.juego_terminado_sin_intentos_restantes_o_todas_las_im_genes_acertadas), Toast.LENGTH_LONG).show()

                    // Navegar a la pantalla de puntuación
                    val intent = Intent(requireContext(), PuntuacionActivity::class.java)
                    intent.putExtra("imagenesAcertadas", ArrayList(viewModel.imagenesAcertadas.value ?: emptyList()))
                    intent.putExtra("puntuacion", viewModel.puntuacion.value ?: 0)
                    val tamaniolista=viewModel.obtenerImagenesSegunDificultad(viewModel.getDificultad()).size
                    val tamanioAcertadas=viewModel.imagenesAcertadas.value?.size?:0
                    if(tamanioAcertadas==tamaniolista){
                        sonido();
                    }
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

        // Dejar esta función aqui porque no se puede acceder a los string desde el viewModel sin modificarlo
        private fun obtenerPista(userPoint: GeoPoint, correctPoint: GeoPoint): String {
            // Calculamos la diferencia entre latitudes y longitudes
            val latitudDiferencia = abs(userPoint.latitude - correctPoint.latitude)
            val longitudDiferencia = abs(userPoint.longitude - correctPoint.longitude)

            // Comparamos las diferencias
            return if (latitudDiferencia > longitudDiferencia) {
                // Si la diferencia de latitudes es mayor
                if (userPoint.latitude < correctPoint.latitude) {
                    getString(R.string.norte)
                } else {
                    getString(R.string.sur)
                }
            } else {
                // Si la diferencia de longitudes es mayor
                if (userPoint.longitude < correctPoint.longitude) {
                    getString(R.string.este)
                } else {
                    getString(R.string.oeste)
                }
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
        private fun sonido() {
           val reproducir=prefs.getBoolean("isMuted",false)
            if(!reproducir){
                mediaPlayer= MediaPlayer.create(requireContext(),R.raw.sonidowin)
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener {
                    mediaPlayer.release()
                }
            }
        }
    }
