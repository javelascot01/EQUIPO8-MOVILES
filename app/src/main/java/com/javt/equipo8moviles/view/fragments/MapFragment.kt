package com.javt.equipo8moviles.view.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.javt.equipo8moviles.R
import com.javt.equipo8moviles.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), MapEventsReceiver {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var marker: Marker? = null
    private  var circleOverlay: Polygon? = null
    private val MULTIPLE_PERMISSION_REQUEST_CODE: Int = 4
    private lateinit var mapView: MapView

    private lateinit var binding: FragmentMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //handle permissions first, before map is created.
        checkPermissionsState()
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName())



        binding= FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root
        mapView = binding.mapView
        setupMap()
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun checkPermissionsState() {
        val fineLocationPermissionCheck = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (fineLocationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ),
                MULTIPLE_PERMISSION_REQUEST_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MULTIPLE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    var somePermissionWasDenied = false
                    for (result in grantResults) {
                        if (result == PackageManager.PERMISSION_DENIED) {
                            somePermissionWasDenied = true
                        }
                    }
                    if (somePermissionWasDenied) {
                        Toast.makeText(
                            requireContext(),
                            "Cant load maps without all the permissions granted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Cant load maps without all the permissions granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }

        }
    }
    private fun setupMap() {
        // Configurar el mapa
        mapView.setClickable(true)
        mapView.setDestroyMode(false)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.getLocalVisibleRect(Rect())

        // Centrar el mapa en España
        val startPoint = GeoPoint(40.4168, -3.7038) // Madrid, España
        val mapController = mapView.controller
        mapController.setZoom(6.8) // Nivel de zoom adecuado para ver España
        mapController.setCenter(startPoint)
        // Añadir MapEventsOverlay
        val mapEventsOverlay = MapEventsOverlay(this)
        mapView.overlays.add(mapEventsOverlay)

        // Brújula
//        val compassOverlay = CompassOverlay(requireContext(), InternalCompassOrientationProvider(requireContext()), mapView)
//        compassOverlay.enableCompass()
//        mapView.overlays.add(compassOverlay)

        // Barra de escala
//        val dm: DisplayMetrics = this.resources.displayMetrics
//        val scaleBarOverlay = ScaleBarOverlay(mapView)
//        scaleBarOverlay.setCentred(true)
//        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 40)
//        mapView.overlays.add(scaleBarOverlay)
    }


    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        if (p == null) return false

        // Eliminar marcador y círculo anteriores
        marker?.let { mapView.overlays.remove(it) }
        circleOverlay?.let { mapView.overlays.remove(it) }

        // Crear y agregar marcador
        marker = Marker(mapView).apply {
            position = p
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapView.overlays.add(marker)

        // Dibujar círculo de 100 km
        circleOverlay = createCircle(p, 100000.0)
        mapView.overlays.add(circleOverlay)

        mapView.invalidate() // Refrescar mapa
        return true
    }
    private fun createCircle(center: GeoPoint, radius: Double): Polygon {
        return Polygon().apply {
            points = Polygon.pointsAsCircle(center, radius)
            fillColor = 0x40FF0000 // Rojo semitransparente
            strokeColor = Color.RED
            strokeWidth = 3f
        }
    }


    override fun longPressHelper(p: GeoPoint?): Boolean {
        return false
    }
}