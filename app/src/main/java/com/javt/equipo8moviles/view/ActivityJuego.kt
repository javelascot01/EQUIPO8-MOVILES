package com.javt.equipo8moviles.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.graphics.Rect
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.javt.equipo8moviles.R
import com.javt.equipo8moviles.databinding.ActivityJuegoBinding
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider

class ActivityJuego : AppCompatActivity(), MapEventsReceiver {
    private lateinit var binding: ActivityJuegoBinding
    private val MULTIPLE_PERMISSION_REQUEST_CODE: Int = 4
    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //handle permissions first, before map is created.
        checkPermissionsState()

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().setUserAgentValue(getPackageName())
        binding = ActivityJuegoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreImagen = intent.getStringExtra("nombreImagen")
        val idImagen = resources.getIdentifier(nombreImagen, "drawable", packageName)
        binding.imagenJuego.setImageResource(idImagen)
        mapView = binding.mapaJuego


        setupMap()

    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    private fun checkPermissionsState() {
        val fineLocationPermissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (fineLocationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
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
                            this,
                            "Cant load maps without all the permissions granted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
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
        mapController.setZoom(6.0) // Nivel de zoom adecuado para ver España
        mapController.setCenter(startPoint)

        // Brújula
        val compassOverlay = CompassOverlay(this, InternalCompassOrientationProvider(this), mapView)
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)

        // Barra de escala
        val dm: DisplayMetrics = this.resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 40)
        mapView.overlays.add(scaleBarOverlay)
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        TODO("Not yet implemented")
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        TODO("Not yet implemented")
    }
}