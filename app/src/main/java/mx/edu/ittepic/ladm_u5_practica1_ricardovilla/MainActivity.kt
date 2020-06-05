package mx.edu.ittepic.ladm_u5_practica1_ricardovilla

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    var baseRemota = FirebaseFirestore.getInstance()
    var posicion = ArrayList<Data>()
    lateinit var locacion : LocationManager
    private lateinit var mMap: GoogleMap
    lateinit var ubicacionCliente : FusedLocationProviderClient
    var lat = 0f
    var long = 0f
    var nombreLugar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTitle("LADM_U5_Practica1_RicardoVilla")

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )

        }

        baseRemota.collection("tecnologico")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {

                    return@addSnapshotListener
                }

                var resultado = ""
                posicion.clear()

                for (document in querySnapshot!!) {

                    var data = Data()
                    data.nombre = document.getString("nombre").toString()
                    data.posicion1 = document.getGeoPoint("posicion1")!!
                    data.posicion2 = document.getGeoPoint("posicion2")!!

                    resultado += data.toString() + "\n\n"
                    posicion.add(data)
                }

                //textView.setText(resultado)
            }



        locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var oyente = Oyente(this)
        //cuando exista una actualizacion se llama a la clase oyente
        locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,01f,oyente)



        button2.setOnClickListener {
            if(editText.text.isEmpty()){
                Toast.makeText(this,"DEBE PONER EL LUGAR QUE DESEA BUSCAR", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            buscarLugar(editText.text.toString())
        }
    }

    private fun miUbicacion() {

        //NO ES UBICACION ACTUAL, ES ULTIMA CONOCIDAD
        //BUSCAR FORMA DE OBTENER UBICACION GPS
        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
            var geoPosicion = GeoPoint(it.latitude, it.longitude)
            textView2.setText("${it.latitude},it.${it.longitude}")

            for (item in posicion) {

                if (item.estoyEn(geoPosicion) == true) {

                    AlertDialog.Builder(this)
                        .setMessage("USTED SE ENCUENTRA EN: " + item.nombre)
                        .setTitle("ATENCION")
                        .setPositiveButton("OK") { p, q -> }
                        .show()
                }

            }
        }.addOnFailureListener {
            textView2.setText("ERROR AL OBTENER UBICACION")
        }
    }


    private fun buscarLugar(valor: String) {
        baseRemota.collection("tecnologico")
            .whereEqualTo("nombre",valor)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    Toast.makeText(this,"ERROR NO SE PUEDE ACCEDER A CONSULTA", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                textView_res.setText("SIN DATA")

                var cadena =""
                for(document in querySnapshot!!)

                {

                    var data = Data()
                    data.posicion1 = document.getGeoPoint("posicion1")!!
                    data.posicion2 = document.getGeoPoint("posicion2")!!
                    data.toString()

                    cadena = "RESULTADO: "+"\nDe "+data.posicion1.latitude + "," +data.posicion1.longitude + "\nhasta "+data.posicion2.latitude + "," + data.posicion2.longitude

                    lat = data.posicion1.latitude.toFloat()
                    long = data.posicion1.longitude.toFloat()
                    nombreLugar = valor

                    textView_res.setText(cadena)

                    var v = Intent(this,MapsActivity::class.java)
                    v.putExtra("lat",lat)
                    v.putExtra("long",long)
                    v.putExtra("lugar",nombreLugar)
                    startActivity(v)








                }




            }

    }




}



//CLASE PARA OBTENER POSICION ACTUAL
class Oyente(puntero:MainActivity): LocationListener {

    var p = puntero
    override fun onLocationChanged(location: Location) {

        p.textView2.setText("POSICION ACTUAL: "+"${location.latitude}, ${location.longitude}")
        p.textView3.setText("")
        var geoPosicionGPS = GeoPoint(location.latitude,location.longitude)

        for (item in p.posicion){
            if(item.estoyEn(geoPosicionGPS)){
                p.textView3.setText("ESTAS EN : ${item.nombre}")
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }




}
