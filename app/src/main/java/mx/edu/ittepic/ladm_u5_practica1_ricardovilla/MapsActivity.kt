package mx.edu.ittepic.ladm_u5_practica1_ricardovilla

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var ubicacionCliente : FusedLocationProviderClient
    var lat = 0f
    var long = 0f
    var nombreLugar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        var extras = intent.extras
        lat = extras!!.getFloat("lat")
        long = extras!!.getFloat("long")
        nombreLugar = extras!!.getString("lugar")!!
        setTitle(nombreLugar)
        ubicacionCliente = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val lugar = LatLng(lat.toDouble(), long.toDouble())
        mMap.addMarker(MarkerOptions().position(lugar).title(nombreLugar))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lugar))

        //Activar controles de zoom
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true
        ubicacionCliente.lastLocation.addOnSuccessListener {it->

            //it.latitude;it.longitude
            if(it!=null){
                val posicionActual = LatLng(it.latitude,it.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicionActual,15f))
            }
        }
    }
}
